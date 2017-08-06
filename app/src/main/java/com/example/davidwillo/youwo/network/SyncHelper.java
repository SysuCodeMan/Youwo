package com.example.davidwillo.youwo.network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.davidwillo.youwo.application.MyApplication;
import com.example.davidwillo.youwo.life.account.AccountDBdao;
import com.example.davidwillo.youwo.life.account.MyDBOpenHelper;
import com.example.davidwillo.youwo.life.express.Express;
import com.example.davidwillo.youwo.life.express.ExpressHandleData;
import com.example.davidwillo.youwo.life.express.RecordSQLiteOpenHelper;
import com.example.davidwillo.youwo.network.model.Course;
import com.example.davidwillo.youwo.network.model.LifeAccount;
import com.example.davidwillo.youwo.network.model.StepRecord;
import com.example.davidwillo.youwo.sport.StepCountDB;
import com.example.davidwillo.youwo.network.model.Homework;
import com.example.davidwillo.youwo.study.CourseDB;
import com.example.davidwillo.youwo.study.HomeworkDB;
import com.example.davidwillo.youwo.util.MessageEvent;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by davidwillo on 6/14/17.
 */

public class SyncHelper {
    private static Gson gson = new Gson();

    private static StepCountDB stepCountDB;
    private static AccountDBdao accountDBdao;
    private static MyDBOpenHelper myDBOpenHelper;
    private static ExpressHandleData expressHandleData;
    private static RecordSQLiteOpenHelper recordSQLiteOpenHelper;
    private static Express express;
    private static CourseDB courseDB;
    private static HomeworkDB homeworkDB;

    public static void  downloadStepRecord(final Context context) {
        stepCountDB = new StepCountDB(context);

        NetworkUtil.getAPI().downloadStepRecord(MyApplication.getInstance().getUsername())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<HttpResult<List<StepRecord>>, List<StepRecord>>() {
                    @Override
                    public List<StepRecord> apply(HttpResult<List<StepRecord>> result) throws Exception {
                        if (result.getResultCode() == 0) {
                            throw new NetworkException(result.getResultMessage());
                        } else {
                            Toast.makeText(context, "历史步数更新成功", Toast.LENGTH_SHORT).show();
                            return result.getData();
                        }
                    }
                })
                .flatMap(new Function<List<StepRecord>, ObservableSource<StepRecord>>() {
                    @Override
                    public ObservableSource<StepRecord> apply(@NonNull List<StepRecord> stepRecords) throws Exception {
                        return Observable.fromIterable(stepRecords);
                    }
                })
                .subscribe(new Observer<StepRecord>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull StepRecord stepRecord) {
                        saveStepCountToDB(stepRecord);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        stepCountDB.close();
                    }

                    @Override
                    public void onComplete() {
                        stepCountDB.close();

                        //uploadStepRecord(context);
                    }
                });
    }


    public static void saveStepCountToDB(StepRecord stepRecord) {
        if (stepCountDB.checkExist(stepRecord.name, stepRecord.month, stepRecord.day)) {
            stepCountDB.deleteSingleFromDB(stepRecord.name, stepRecord.month, stepRecord.day);
            stepCountDB.insert2DB(stepRecord.name, stepRecord.month, stepRecord.day, stepRecord.count);
        } else {
            stepCountDB.insert2DB(stepRecord.name, stepRecord.month, stepRecord.day, stepRecord.count);
        }
    }

    public static void uploadStepRecord(final Context context) {
        stepCountDB = new StepCountDB(context);
        List<StepRecord> history = stepCountDB.query(MyApplication.getInstance().getUsername());
        NetworkUtil.getAPI().uploadStepRecord(MyApplication.getInstance().getUsername(),gson.toJson(history))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<HttpResult<String>, String>() {
                    @Override
                    public String apply(HttpResult<String> result) throws Exception {
                        if (result.getResultCode() == 0) {
                            throw new NetworkException(result.getResultMessage());
                        } else {
                            Toast.makeText(context, "历史步数上传成功", Toast.LENGTH_SHORT).show();
                            return result.getData();
                        }
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        stepCountDB.close();
                    }

                    @Override
                    public void onComplete() {
                        stepCountDB.close();

                        downloadStepRecord(context);
                    }
                });
    }

    public static void downloadLifeAccount(final Context context) {
        accountDBdao = new AccountDBdao(context);
        myDBOpenHelper = new MyDBOpenHelper(context);
        NetworkUtil.getAPI().downloadLifeAccount(MyApplication.getInstance().getUsername())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<HttpResult<List<LifeAccount>>, List<LifeAccount>>() {
                    @Override
                    public List<LifeAccount> apply(HttpResult<List<LifeAccount>> result) throws Exception {
                        if (result.getResultCode() == 0) {
                            throw new NetworkException(result.getResultMessage());
                        } else {
                            Toast.makeText(context, "历史账单更新成功", Toast.LENGTH_SHORT).show();
                            return result.getData();
                        }
                    }
                })
                .flatMap(new Function<List<LifeAccount>, ObservableSource<LifeAccount>>() {
                    @Override
                    public ObservableSource<LifeAccount> apply(@NonNull List<LifeAccount> lifeAccounts) throws Exception {
                        return Observable.fromIterable(lifeAccounts);
                    }
                })
                .subscribe(new Observer<LifeAccount>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull LifeAccount lifeAccount) {
                        // insert into DB
                        saveLifeAccountToDB(lifeAccount);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(context, "网络请求出错，请稍后再试", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        myDBOpenHelper.close();
                    }

                    @Override
                    public void onComplete() {
                        myDBOpenHelper.close();
                        EventBus.getDefault().post(new MessageEvent(" "));
                        //uploadLifeAccount(context);
                    }
                });
    }

    public static void saveLifeAccountToDB(LifeAccount lifeAccount) {
        if (accountDBdao.checkExist(lifeAccount.getTime())) {
            String accoutid = String.valueOf(lifeAccount.id);
            accountDBdao.update(accoutid, lifeAccount.time, lifeAccount.money, lifeAccount.type,
            lifeAccount.earnings, lifeAccount.remark,lifeAccount.name);
        } else {
            accountDBdao.add(lifeAccount.time, lifeAccount.money, lifeAccount.type, lifeAccount.earnings,
            lifeAccount.remark, lifeAccount.name);
        }
    }

    public static void uploadLifeAccount(final Context context) {
        accountDBdao = new AccountDBdao(context);
        myDBOpenHelper = new MyDBOpenHelper(context);
        List<LifeAccount> lifeAccountList = accountDBdao.findAllByName(MyApplication.getInstance().getUsername());
        Log.e("lifeAccountList:",gson.toJson(lifeAccountList));
        NetworkUtil.getAPI().uploadLifeAccount(MyApplication.getInstance().getUsername(),gson.toJson(lifeAccountList))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<HttpResult<String>, String>() {
                    @Override
                    public String apply(@NonNull HttpResult<String> stringHttpResult) throws Exception {
                        if (stringHttpResult.getResultCode() == 0) {
                            throw new NetworkException(stringHttpResult.getResultMessage());
                        } else {
                            Toast.makeText(context, "历史账单上传成功", Toast.LENGTH_SHORT).show();
                            return stringHttpResult.getData();
                        }
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(context, "网络请求出错，请稍后再试", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        myDBOpenHelper.close();
                    }

                    @Override
                    public void onComplete() {
                        myDBOpenHelper.close();

                        downloadLifeAccount(context);
                    }
                });
    }

//    public static void downloadLifeExpress(final Context context) {
//        expressHandleData = new ExpressHandleData(context);
//        recordSQLiteOpenHelper = new RecordSQLiteOpenHelper(context);
//
//        NetworkUtil.getAPI().downloadLifeExpress(MyApplication.getInstance().getUsername())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map(new Function<HttpResult<List<LifeExpress>>, List<LifeExpress>>() {
//                    @Override
//                    public List<LifeExpress> apply(@NonNull HttpResult<List<LifeExpress>> listHttpResult) throws Exception {
//                        if (listHttpResult.getResultCode() == 0) {
//                            throw new NetworkException(listHttpResult.getResultMessage());
//                        } else {
//                            return listHttpResult.getData();
//                        }
//                    }
//                })
//                .flatMap(new Function<List<LifeExpress>, ObservableSource<LifeExpress>>() {
//                    @Override
//                    public ObservableSource<LifeExpress> apply(@NonNull List<LifeExpress> lifeExpresses) throws Exception {
//                        return Observable.fromIterable(lifeExpresses);
//                    }
//                })
//                .subscribe(new Observer<LifeExpress>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(@NonNull LifeExpress lifeExpress) {
//                        // do sth
//                        saveExpressCountToDB(lifeExpress);
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//                        e.printStackTrace();
//                        recordSQLiteOpenHelper.close();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        recordSQLiteOpenHelper.close();
//                    }
//                });
//    }
//
//    public static void saveExpressCountToDB(LifeExpress lifeExpress) {
//        if (expressHandleData.hasData(lifeExpress.ExpressNum)) {
//
//        } else {
//            expressHandleData.insertData(lifeExpress.ExpressNum);
//        }
//    }

//    public static void uploadLifeExpress(final Context context, List<Content> datalist) {
//        express = new Express();
//        express.setContent(datalist);
//        expressHandleData = new ExpressHandleData(context);
//        recordSQLiteOpenHelper = new RecordSQLiteOpenHelper(context);
//        NetworkUtil.getAPI().uploadLifeExpress(MyApplication.getInstance().getUsername(),gson.toJson(datalist))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map(new Function<HttpResult<String>, String>() {
//                    @Override
//                    public String apply(@NonNull HttpResult<String> stringHttpResult) throws Exception {
//                        if (stringHttpResult.getResultCode() == 0) {
//                            throw new NetworkException(stringHttpResult.getResultMessage());
//                        } else {
//                            return stringHttpResult.getData();
//                        }
//                    }
//                })
//                .subscribe(new Observer<String>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(@NonNull String s) {
//
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        Toast.makeText(context, "网络请求出错，请稍后再试", Toast.LENGTH_SHORT).show();
//                        e.printStackTrace();
//                        recordSQLiteOpenHelper.close();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        recordSQLiteOpenHelper.close();
//                        downloadLifeExpress(context);
//
//                    }
//                });
//    }





    public static void  downloadCourseRecord(final Context context) {
        courseDB = new CourseDB(context);
        final ArrayList<Course> courses = new ArrayList<>();
        NetworkUtil.getAPI().downloadCourseRecord(MyApplication.getInstance().getUsername())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<HttpResult<List<Course>>, List<Course>>() {
                    @Override
                    public List<Course> apply(HttpResult<List<Course>> result) throws Exception {
                        if (result.getResultCode() == 0) {
                            throw new NetworkException(result.getResultMessage());
                        } else {
                            Toast.makeText(context, "课程数据下载成功", Toast.LENGTH_SHORT).show();
                            return result.getData();
                        }
                    }
                })
                .flatMap(new Function<List<Course>, ObservableSource<Course>>() {
                    @Override
                    public ObservableSource<Course> apply(@NonNull List<Course> courses) throws Exception {
                        return Observable.fromIterable(courses);
                    }
                })
                .subscribe(new Observer<Course>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Course course) {
                        courses.add(course);

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        courseDB.close();
                    }

                    @Override
                    public void onComplete() {
                        courseDB.close();
                        saveCourseRecordToDB(courses);
                        EventBus.getDefault().post(new MessageEvent2(" "));
                    }
                });
    }


    public static void saveCourseRecordToDB(ArrayList<Course> courses) {
        /*if (CourseDB.checkExist(MyApplication.getInstance().getUsername())) {
            CourseDB.UpdateRecord(MyApplication.getInstance().getUsername(), courses);
        }*/
//        ArrayList<Course> courseArrayList = new ArrayList<>();
//        courseArrayList.add(courses);
        courseDB.UpdateRecord(MyApplication.getInstance().getUsername(), courses);
    }

    public static void uploadCourseRecord(final Context context) {
        courseDB = new CourseDB(context);

        ArrayList<Course> history = courseDB.queryCourses(MyApplication.getInstance().getUsername());
        NetworkUtil.getAPI().uploadCourseRecord(MyApplication.getInstance().getUsername(),gson.toJson(history))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<HttpResult<String>, String>() {
                    @Override
                    public String apply(HttpResult<String> result) throws Exception {
                        if (result.getResultCode() == 0) {
                            throw new NetworkException(result.getResultMessage());
                        } else {
                            Toast.makeText(context, "课程数据上传成功", Toast.LENGTH_SHORT).show();
                            return result.getData();
                        }
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        courseDB.close();
                    }

                    @Override
                    public void onComplete() {
                        courseDB.close();
                        downloadCourseRecord(context);

                    }
                });
    }

    public static void  downloadHomeworkRecord(final Context context) {
        homeworkDB = new HomeworkDB(context);

        NetworkUtil.getAPI().downloadHomeworkRecord(MyApplication.getInstance().getUsername())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<HttpResult<List<Homework>>, List<Homework>>() {
                    @Override
                    public List<Homework> apply(HttpResult<List<Homework>> result) throws Exception {
                        if (result.getResultCode() == 0) {
                            throw new NetworkException(result.getResultMessage());
                        } else {
                            Toast.makeText(context, "作业下载成功", Toast.LENGTH_SHORT).show();
                            return result.getData();
                        }
                    }
                })
                .subscribe(new Observer<List<Homework>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<Homework> homeworks) {
                        homeworkDB.updateAllHomework(MyApplication.getInstance().getUsername(), homeworks);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        homeworkDB.close();
                    }

                    @Override
                    public void onComplete() {
                        homeworkDB.close();

                    }
                });
    }


//    public static void  downloadHomeworkRecord(final Context context) {
//        homeworkDB = new HomeworkDB(context);
//
//        NetworkUtil.getAPI().downloadHomeworkRecord(MyApplication.getInstance().getUsername())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map(new Function<HttpResult<List<Homework>>, List<Homework>>() {
//                    @Override
//                    public List<Homework> apply(HttpResult<List<Homework>> result) throws Exception {
//                        if (result.getResultCode() == 0) {
//                            throw new NetworkException(result.getResultMessage());
//                        } else {
//                            Toast.makeText(context, "作业下载成功", Toast.LENGTH_SHORT).show();
//                            return result.getData();
//                        }
//                    }
//                })
//                .flatMap(new Function<List<Homework>, ObservableSource<Homework>>() {
//                    @Override
//                    public ObservableSource<Homework> apply(@NonNull List<Homework> homeworks) throws Exception {
//                        return Observable.fromIterable(homeworks);
//                    }
//                })
//                .subscribe(new Observer<Homework>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(@NonNull Homework homework) {
//                        saveHomeworkRecordToDB(homework, homework.getCoursename());
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//                        homeworkDB.close();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        homeworkDB.close();
//
//                    }
//                });
//    }
//
//
//    public static void saveHomeworkRecordToDB(Homework homework, String course) {
////        if (homeworkDB.checkExist(MyApplication.getInstance().getUsername(), course)) {
////            homeworkDB.updateHomework(MyApplication.getInstance().getUsername(), course, homework.getHwDescription(), homework.getFinished());
////        } else {
//            homeworkDB.insertHomework(course, homework.getFinished(), homework.getHwDescription(), MyApplication.getInstance().getUsername());
//        //}
//    }

    public static void uploadHomeworkRecord(final Context context) {
        homeworkDB = new HomeworkDB(context);
        ArrayList<Homework> history = homeworkDB.queryAllHomeworks(MyApplication.getInstance().getUsername());
        Log.e("Homework", gson.toJson(history));
        NetworkUtil.getAPI().uploadHomeworkRecord(MyApplication.getInstance().getUsername(),gson.toJson(history))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<HttpResult<String>, String>() {
                    @Override
                    public String apply(HttpResult<String> result) throws Exception {
                        if (result.getResultCode() == 0) {
                            throw new NetworkException(result.getResultMessage());
                        } else {
                            Toast.makeText(context, "作业上传成功", Toast.LENGTH_SHORT).show();
                            return result.getData();
                        }
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        homeworkDB.close();
                    }

                    @Override
                    public void onComplete() {
                        homeworkDB.close();
                        downloadHomeworkRecord(context);

                    }
                });
    }

}
