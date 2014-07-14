package com.vivavu.dream.repository;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.vivavu.dream.common.Constants;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.RestTemplateFactory;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.model.LoginInfo;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.SecureToken;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.BucketGroup;
import com.vivavu.dream.model.bucket.Today;
import com.vivavu.dream.model.bucket.TodayGroup;
import com.vivavu.dream.util.JsonFactory;
import com.vivavu.dream.util.RestTemplateUtils;

import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yuja on 14. 1. 13.
 */
public class DataRepository {
    public static String TAG = "com.vivavu.dream.repository.DataRepository";
    private static DreamApp context;
    private static DatabaseHelper databaseHelper;

    public DataRepository() {
        this.context = DreamApp.getInstance();
        databaseHelper = new DatabaseHelper(getContext());
    }

    private static HttpHeaders getBasicAuthHeader(){

        if(getContext().getTokenType() !=null &&  !"facebook".equals(getContext().getTokenType())){
            return RestTemplateUtils.getBasicAuthHeader(DreamApp.getInstance().getToken(), "unused");
        }
        else{
            return RestTemplateUtils.getBasicAuthHeader(DreamApp.getInstance().getToken(), "facebook");
        }

    }

    public static ResponseBodyWrapped<SecureToken> registUser(LoginInfo loginInfo){
        loginInfo.setCommand("register");
        RestTemplate restTemplate = RestTemplateFactory.getInstance();

        HttpEntity request = new HttpEntity<LoginInfo>(loginInfo);
        ResponseEntity<String> result = null;
        try{
            result = restTemplate.exchange(Constants.apiUsers, HttpMethod.POST, request, String.class);
        } catch (ResourceAccessException timeoutException){
	        Log.e("dream", timeoutException.toString());
	        if(timeoutException.getCause() instanceof ConnectTimeoutException){
		        return new ResponseBodyWrapped<SecureToken>(ResponseStatus.TIMEOUT, "서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.", null);
	        }
        }catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        Gson gson = JsonFactory.getInstance();
        Type type = new TypeToken<ResponseBodyWrapped<SecureToken>>(){}.getType();
        ResponseBodyWrapped<SecureToken> responseBodyWrapped = gson.fromJson(String.valueOf(result.getBody()), type);

        return responseBodyWrapped;
    }

    public static void deleteBucket(Integer bucketId){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader();
        HttpEntity request = new HttpEntity<String>(requestHeaders);

        ResponseEntity<String> result = null;
        try {
            result = restTemplate.exchange(Constants.apiBucketInfo, HttpMethod.DELETE, request, String.class, bucketId);
            return ;
        } catch (ResourceAccessException timeoutException){
	        Log.e("dream", timeoutException.toString());
	        if(timeoutException.getCause() instanceof ConnectTimeoutException){
		        return ;
	        }
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }
        Log.d("dream", String.valueOf(result));

        return ;
    }

    public static DreamApp getContext() {
        if(context == null){
            context = DreamApp.getInstance();
        }
        return context;
    }

    public static void setContext(DreamApp context) {
        DataRepository.context = context;
    }

    public static DatabaseHelper getDatabaseHelper() {
        if(databaseHelper == null){
            databaseHelper = new DatabaseHelper(getContext());
            if(getContext().getUser() != null) {
                deleteBucketsNotEqualUserId(getContext().getUser().getId());
            }
        }
        return databaseHelper;
    }

    public static void saveBuckets(List<Bucket> list){
        deleteAllBuckets();
        for(Bucket bucket : list){
            saveBucket(bucket);
        }
    }

    public static void saveBucket(Bucket bucket){
        if(bucket.getId() != null) {
            getDatabaseHelper().getBucketRuntimeDao().createOrUpdate(bucket);
        }
    }

    public static void deleteBucket(Bucket bucket)
    {
        if (bucket != null && bucket.getId() != null) {
            int row = getDatabaseHelper().getBucketRuntimeDao().delete(bucket);
            Log.v("aaaa", String.valueOf(row));
        }
    }

    public static void deleteAllBuckets(){
        DeleteBuilder<Bucket,Integer> deleteBuilder = getDatabaseHelper().getBucketRuntimeDao().deleteBuilder();
        try {
            deleteBuilder.delete();
        } catch (SQLException e) {
            Log.e("dream", e.getMessage());
        }
    }

    public static void deleteBucketsNotEqualUserId(int userId){
        DeleteBuilder<Bucket,Integer> deleteBuilder = getDatabaseHelper().getBucketRuntimeDao().deleteBuilder();
        try {
            Where<Bucket, Integer> where = deleteBuilder.where();
            where.ne("userId", userId);
            deleteBuilder.delete();
        } catch (SQLException e) {
            Log.e("dream", e.getMessage());
        }
    }

    public static void deleteBucketsEqualUserId(int userId){
        DeleteBuilder<Bucket,Integer> deleteBuilder = getDatabaseHelper().getBucketRuntimeDao().deleteBuilder();
        try {
            Where<Bucket, Integer> where = deleteBuilder.where();
            where.eq("userId", userId);
            deleteBuilder.delete();
        } catch (SQLException e) {
            Log.e("dream", e.getMessage());
        }
    }

    public static List<Bucket> listBucketByRange(String range){
        List<Bucket> list = null;
        try {
            QueryBuilder<Bucket, Integer> qb2 = getDatabaseHelper().getBucketRuntimeDao().queryBuilder();
            Where where = qb2.where();
            if (range == null) {
                where.isNull("range");
            } else {
                where.eq("range", range);
            }
            qb2.orderBy("deadline", true);
            list = qb2.query();
        }  catch (SQLException e) {
            Log.e("dream", e.getMessage());
        }

        return list;
    }
    public static List<BucketGroup> listBucketGroup(){
        QueryBuilder<Bucket, Integer> qb = getDatabaseHelper().getBucketRuntimeDao().queryBuilder();
        qb.groupBy("range");
        qb.orderBy("range", true);
        qb.orderBy("deadline", true);
        qb.orderBy("id", true);
        List<BucketGroup> bucketGroups = null;
        try {
            List<Bucket> rangeList = qb.query();
            bucketGroups = makeShelfList(rangeList);

            for(BucketGroup range : bucketGroups){
                List<Bucket> list = listBucketByRange(range.getRange());
                range.setBukets(list);
            }
        } catch (SQLException e) {
            Log.e("dream", e.getMessage());
        }
        return bucketGroups;
    }

    public static List<BucketGroup> makeShelfList(List<Bucket> list){
        List<BucketGroup> returnList = new ArrayList<BucketGroup>();
        returnList.add(new BucketGroup());
        final int maxIndex = 7;
        for (int i = 1; i < maxIndex; i++) {
            returnList.add(new BucketGroup(String.valueOf(i*10)));
        }

        for (Bucket bucket : list){
            String range = bucket.getRange();
            if (range != null) {
                Integer numRange = Integer.parseInt(range);
                if(numRange >= maxIndex * 10){
                    returnList.add(new BucketGroup(String.valueOf(range)));
                }
            }
        }

        return returnList;
    }

    public static Bucket getBucket(Integer id){
        RuntimeExceptionDao<Bucket,Integer> bucketRuntimeDao = getDatabaseHelper().getBucketRuntimeDao();
        Bucket bucket = null;
        if(id != null) {
            bucket = bucketRuntimeDao.queryForId(id);
        }
        if(bucket == null){
            bucket = new Bucket();
        }
        return bucket;
    }

    public static void saveTodays(List<Today> list){
        /*Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        deleteTodays(cal.getTime());*/
        updateTodayGroup();
        for(Today today : list){
            saveToday(today);
        }
    }

    public static void saveToday(Today today){
        if(today.getId() != null) {
            getDatabaseHelper().getTodayRuntimeDao().createOrUpdate(today);
        }
    }

    public static void deleteAllTodays(){
        DeleteBuilder<Today,Integer> deleteBuilder = getDatabaseHelper().getTodayRuntimeDao().deleteBuilder();
        try {
            deleteBuilder.delete();
        } catch (SQLException e) {
            Log.e("dream", e.getMessage());
        }
    }

    public static void deleteTodays(Date date){
        DeleteBuilder<Today,Integer> deleteBuilder = getDatabaseHelper().getTodayRuntimeDao().deleteBuilder();
        try {
            Where<Today, Integer> where = deleteBuilder.where();
            where.lt("date", date);
            deleteBuilder.delete();
        } catch (SQLException e) {
            Log.e("dream", e.getMessage());
        }
    }

    public static List<Today> listToday(){
        List<Today> list = null;
        QueryBuilder<Today, Integer> todayQueryBuilder = getDatabaseHelper().getTodayRuntimeDao().queryBuilder();

        todayQueryBuilder.orderBy("deadline", true);
        todayQueryBuilder.orderBy("id", true);

        try {
            list = todayQueryBuilder.query();
        } catch (SQLException e) {
            Log.e("dream", e.getMessage());
            list = new ArrayList<Today>();
        }

        return list;
    }

    public static List<TodayGroup> listTodayGroupAndTodayData(){
         List<TodayGroup> todayGroups = null;
        try {
            todayGroups = listTodayGroup();

            for(TodayGroup todayGroup : todayGroups){
                QueryBuilder<Today, Integer> todayQueryBuilder = getDatabaseHelper().getTodayRuntimeDao().queryBuilder();
                Where where = todayQueryBuilder.where();
                if(todayGroup.getDate() == null){
                    where.isNull("date");
                }else{
                    where.eq("date", todayGroup.getDate());
                }
                todayQueryBuilder.orderBy("deadline", true);
                todayQueryBuilder.orderBy("id", true);

                List<Today> list = todayQueryBuilder.query();
                todayGroup.setTodayList(list);
            }
        } catch (SQLException e) {
            todayGroups = new ArrayList<TodayGroup>();
            Log.e("dream", e.getMessage());
        }
        return todayGroups;
    }

    public static void saveTodayGroups(List<TodayGroup> todayGroupList){
        for (TodayGroup todayGroup : todayGroupList){
            saveTodayGroup(todayGroup);
        }
    }
    public static void saveTodayGroup(TodayGroup todayGroup){
        if(todayGroup != null){
            getDatabaseHelper().getTodayGroupRuntimeDao().createOrUpdate(todayGroup);
        }
    }

    public static void updateTodayGroup(){
        deleteAllTodayGroups();
        QueryBuilder<Today, Integer> todayQueryBuilder = getDatabaseHelper().getTodayRuntimeDao().queryBuilder();
        todayQueryBuilder.groupBy("date");
        todayQueryBuilder.orderBy("date", false);
        List<TodayGroup> todayGroupList = new ArrayList<TodayGroup>();
        try {
            List<Today> query = todayQueryBuilder.query();
            for (Today today : query){
                TodayGroup tmp = new TodayGroup();
                tmp.setDate(today.getDate());
                todayGroupList.add(tmp);
            }
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }

        saveTodayGroups(todayGroupList);

    }

    public static List<TodayGroup> listTodayGroup(){
        QueryBuilder<TodayGroup, Date> todayGroupIntegerQueryBuilder = getDatabaseHelper().getTodayGroupRuntimeDao().queryBuilder();
        todayGroupIntegerQueryBuilder.orderBy("date", false);
        List<TodayGroup> today = null;
        try {
            today = todayGroupIntegerQueryBuilder.query();
        } catch (SQLException e) {
            today = new ArrayList<TodayGroup>();
            Log.e(TAG, e.getMessage());
        }

        return today;
    }

    public static Date lastTodayDate(){
        QueryBuilder<TodayGroup, Date> todayGroupIntegerQueryBuilder = getDatabaseHelper().getTodayGroupRuntimeDao().queryBuilder();
        todayGroupIntegerQueryBuilder.orderBy("date", false);
        TodayGroup today = null;
        try {
            today = todayGroupIntegerQueryBuilder.queryForFirst();
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
        if(today == null) {
            return null;
        }else{
            return today.getDate();
        }
    }

    public static boolean isExistsTodayData(Date date){
        List<TodayGroup> todays = getDatabaseHelper().getTodayGroupRuntimeDao().queryForEq("date", date);
        if(todays != null && todays.size() > 0){
            return true;
        }
        return false;
    }

    public static List<Date> getTodayDates(){
        QueryBuilder<TodayGroup, Date> todayGroupIntegerQueryBuilder = getDatabaseHelper().getTodayGroupRuntimeDao().queryBuilder();
        todayGroupIntegerQueryBuilder.orderBy("date", false);
        List<TodayGroup> todayGroups = null;
        List<Date> dateArrayList = new ArrayList<Date>();
        try {
            List<TodayGroup> rangeList = todayGroupIntegerQueryBuilder.query();
            for (TodayGroup today : rangeList){
                dateArrayList.add(today.getDate());
            }

        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }

        return dateArrayList;
    }

    public static void deleteAllTodayGroups(){
        DeleteBuilder<TodayGroup,Date> deleteBuilder = getDatabaseHelper().getTodayGroupRuntimeDao().deleteBuilder();
        try {
            deleteBuilder.delete();
        } catch (SQLException e) {
            Log.e("dream", e.getMessage());
        }
    }

    public static List<Bucket> ListBucketByRptCndt(String weekDay, Integer dayOfWeekInMonth){
        List<Bucket> list = null;
        try {
            Date date = new Date();
            QueryBuilder<Bucket, Integer> queryBuilder = getDatabaseHelper().getBucketRuntimeDao().queryBuilder();
            Where where = queryBuilder.where();
            where.eq("rptType", "WKRP");
            where.and();
            where.eq("status", 0);
            where.and();
            where.gt("deadline",date);
            where.and();
            if (weekDay.equals("Mon")) {
                where.like("rptCndt", "1______");
                where.or();
                where.eq("rptType", "WEEK");
                if (dayOfWeekInMonth == 1){
                    where.or();
                    where.eq("rptType", "MNTH");
                }
            } else if (weekDay.equals("Tue")) {
                where.like("rptCndt","_1_____");
            } else if (weekDay.equals("Wed")) {
                where.like("rptCndt","__1____");
            } else if (weekDay.equals("Thu")) {
                where.like("rptCndt","___1___");
            } else if (weekDay.equals("Fri")) {
                where.like("rptCndt","____1__");
            } else if (weekDay.equals("Sat")) {
                where.like("rptCndt","_____1_");
            } else if (weekDay.equals("Sun")) {
                where.like("rptCndt","______1");
                where.or();
                where.eq("rptType", "WEEK");
                if (dayOfWeekInMonth == -1){
                    where.or();
                    where.eq("rptType", "MNTH");
                }
            }
            list = queryBuilder.query();
        } catch (SQLException e) {
            Log.e("dream", e.getMessage());
        }

        return list;
    }

    public static void clearDb(){
        DeleteBuilder<TodayGroup,Date> deleteBuilder = getDatabaseHelper().getTodayGroupRuntimeDao().deleteBuilder();
        DeleteBuilder<Bucket, Integer> bucketDeleteBuilder = getDatabaseHelper().getBucketRuntimeDao().deleteBuilder();
        DeleteBuilder<Today, Integer> todayDeleteBuilder = getDatabaseHelper().getTodayRuntimeDao().deleteBuilder();
        try {
            deleteBuilder.delete();
            bucketDeleteBuilder.delete();
            todayDeleteBuilder.delete();
        } catch (SQLException e) {
            Log.e("dream", e.getMessage());
        }

    }
}
