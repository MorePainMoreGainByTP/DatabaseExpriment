package com.example.swjtu.databaseexpriment.exercise6;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.entity.Major;
import com.example.swjtu.databaseexpriment.entity.School;
import com.example.swjtu.databaseexpriment.entity.Student;
import com.example.swjtu.databaseexpriment.exercise8.adapter.MyPrintDocumentAdapter;

import org.litepal.crud.DataSupport;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.example.swjtu.databaseexpriment.R.id.group_grade_high;
import static com.example.swjtu.databaseexpriment.R.id.group_grade_low;
import static java.lang.Integer.parseInt;
import static org.litepal.crud.DataSupport.select;

/**
 * Created by tangpeng on 2017/5/10.
 */

public class StudentInfoActivity extends AppCompatActivity {
    private static final String TAG = "StudentInfoActivity";
    private ActionBar actionBar;
    private ViewPager viewPager;
    private LinearLayout bottomLayout;
    private TextView closeButton;
    private TextInputLayout stuNameWra, stuNoWra, classNoWra, studyTimeWra, gradeWra, enrollmentDateWra, birthDateWra;
    private Spinner sexSelector, schoolSelector, majorSelector, categorySelector;
    private Button deleteBtn, saveBtn, clearBtn;

    public List<Fragment> fragmentList;
    private List<Student> studentList;
    private List<School> schoolList;
    private List<Major> majorList;

    private Student currStudent;    //当前选中的学生
    private int currIndex;

    public int firstVisibleIndex = 0;
    private int allCount = 0;
    private SharedPreferences sharedPreferences;
    private boolean saved = false;
    private boolean addData = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e(TAG, "uncaughtException: ", e);
            }
        });
        sharedPreferences = getSharedPreferences("exercise6", MODE_PRIVATE);
        saved = sharedPreferences.getBoolean("save", false);
        //saved = false;
        setContentView(R.layout.activity_student_info);
        setActionBar();
        getViews();
        initData();
        initFragment();
    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
    }

    private void getViews() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        bottomLayout = (LinearLayout) findViewById(R.id.bottomLayout);
        closeButton = (TextView) findViewById(R.id.closeButton);
        stuNameWra = (TextInputLayout) findViewById(R.id.stuNameWrapper);
        stuNoWra = (TextInputLayout) findViewById(R.id.stuNoWrapper);
        classNoWra = (TextInputLayout) findViewById(R.id.classNoWrapper);
        studyTimeWra = (TextInputLayout) findViewById(R.id.studyTimeWrapper);
        gradeWra = (TextInputLayout) findViewById(R.id.gradeWrapper);
        enrollmentDateWra = (TextInputLayout) findViewById(R.id.enrollmentDateWrapper);
        birthDateWra = (TextInputLayout) findViewById(R.id.birthDateWrapper);
        sexSelector = (Spinner) findViewById(R.id.spinnerSex);
        schoolSelector = (Spinner) findViewById(R.id.spinnerSchool);
        majorSelector = (Spinner) findViewById(R.id.spinnerMajor);
        categorySelector = (Spinner) findViewById(R.id.spinnerCategory);
        deleteBtn = (Button) findViewById(R.id.deleteButton);
        saveBtn = (Button) findViewById(R.id.saveButton);
        clearBtn = (Button) findViewById(R.id.clearButton);

        bottomGroupLayout = (LinearLayout) findViewById(R.id.bottomGroupLayout);
        bottomGroupLayout.setVisibility(GONE);
        groupName = (EditText) findViewById(R.id.group_stu_name);
        groupId = (EditText) findViewById(R.id.group_stu_no);
        groupClass = (EditText) findViewById(R.id.group_class_no);
        groupGradeLow = (EditText) findViewById(group_grade_low);
        groupGradeHigh = (EditText) findViewById(group_grade_high);
        groupYearLimit = (EditText) findViewById(R.id.group_study_time);
        groupBirthLow = (EditText) findViewById(R.id.group_birth_date_low);
        groupBirthHigh = (EditText) findViewById(R.id.group_birth_date_high);
        groupNameMatch = (CheckBox) findViewById(R.id.group_name_all_match);
        groupIdMatch = (CheckBox) findViewById(R.id.group_id_all_match);
        groupSpinnerSchool = (Spinner) findViewById(R.id.groupSpinnerSchool);
        groupSpinnerMajor = (Spinner) findViewById(R.id.groupSpinnerMajor);
        groupSpinnerSex = (Spinner) findViewById(R.id.groupSpinnerSex);
        groupSpinnerDegree = (Spinner) findViewById(R.id.groupSpinnerDegree);
        groupRetrieveBtn = (Button) findViewById(R.id.retrieveButton);
        groupResetBtn = (Button) findViewById(R.id.resetButton);
        groupCloseButton = (TextView) findViewById(R.id.groupCloseButton);

        bottomMask = (LinearLayout) findViewById(R.id.bottom_mask);
        editRetrieve = (EditText) findViewById(R.id.mask_condition);

        powerLayout = (LinearLayout) findViewById(R.id.fragment_power_retrieve);
        editValue = (EditText) findViewById(R.id.editValue);
        spinnerField = (Spinner) findViewById(R.id.spinnerField);
        spinnerLogicOption = (Spinner) findViewById(R.id.spinnerLogicOption);
        spinnerOperator = (Spinner) findViewById(R.id.spinnerOperator);
        recyclerView = (RecyclerView) findViewById(R.id.power_recycler);
        powerConditionAdapter = new PowerConditionAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(powerConditionAdapter);

        MyOnClickListener myOnClickListener = new MyOnClickListener(this);
        closeButton.setOnClickListener(myOnClickListener);
        gradeWra.getEditText().setOnClickListener(myOnClickListener);
        enrollmentDateWra.getEditText().setOnClickListener(myOnClickListener);
        birthDateWra.getEditText().setOnClickListener(myOnClickListener);
        deleteBtn.setOnClickListener(myOnClickListener);
        saveBtn.setOnClickListener(myOnClickListener);
        clearBtn.setOnClickListener(myOnClickListener);
        schoolSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateMajorSelector(schoolList.get(position).getSimpleName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        groupCloseButton.setOnClickListener(myOnClickListener);
        groupResetBtn.setOnClickListener(myOnClickListener);
        groupRetrieveBtn.setOnClickListener(myOnClickListener);
        groupGradeHigh.setOnClickListener(myOnClickListener);
        groupGradeLow.setOnClickListener(myOnClickListener);
        groupBirthHigh.setOnClickListener(myOnClickListener);
        groupBirthLow.setOnClickListener(myOnClickListener);
        groupSpinnerSchool.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectXMajor((String) groupSpinnerSchool.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initData() {
        studentList = new ArrayList<>();
        schoolList = new ArrayList<>();
        majorList = new ArrayList<>();

        Date myDate = new Date(System.currentTimeMillis());
        //DataSupport.deleteAll(School.class);
        //DataSupport.deleteAll(Major.class);
        //DataSupport.deleteAll(Student.class);
        studentList = DataSupport.findAll(Student.class);
        allCount = studentList.size();
        Log.i(TAG, "studentList: " + studentList);
        updateTitle();
        if (saved) {
            schoolList = DataSupport.findAll(School.class);
            majorList = DataSupport.findAll(Major.class);
        } else for (int i = 0; i < 5; i++) {
            School school = new School();
            switch (i) {
                case 0:
                    school.setSchoolName("信息科学与技术学院");
                    school.setSimpleName("信息学院");
                    school.setCode("0001");
                    for (int j = 0; j < 4; j++) {
                        Major major = new Major();
                        switch (j) {
                            case 0:
                                major.setSchoolName("信息学院");
                                major.setMajorName("软件工程");
                                major.setSimpleName("软件");
                                major.setCode("0101");
                                break;
                            case 1:
                                major.setSchoolName("信息学院");
                                major.setMajorName("通信工程");
                                major.setSimpleName("通信");
                                major.setCode("0102");
                                break;
                            case 2:
                                major.setSchoolName("信息学院");
                                major.setMajorName("计算机科学与技术");
                                major.setSimpleName("计科");
                                major.setCode("0103");
                                break;
                            case 3:
                                major.setSchoolName("信息学院");
                                major.setMajorName("轨道交通与信号");
                                major.setSimpleName("轨道");
                                major.setCode("0104");
                                break;
                        }
                        majorList.add(major);
                    }
                    break;
                case 1:
                    school.setSchoolName("交通与运输学院");
                    school.setSimpleName("交运学院");
                    school.setCode("0002");
                    for (int j = 0; j < 3; j++) {
                        Major major = new Major();
                        switch (j) {
                            case 0:
                                major.setSchoolName("交运学院");
                                major.setMajorName("交通运输");
                                major.setSimpleName("交运");
                                major.setCode("0201");
                                majorList.add(major);
                                break;
                            case 1:
                                major.setSchoolName("交运学院");
                                major.setMajorName("安全工程");
                                major.setSimpleName("安全");
                                major.setCode("0202");
                                majorList.add(major);
                                break;
                            case 2:
                                major.setSchoolName("交运学院");
                                major.setMajorName("物流管理");
                                major.setSimpleName("物管");
                                major.setCode("0203");
                                majorList.add(major);
                                break;
                        }
                    }
                    break;
                case 2:
                    school.setSchoolName("机械工程学院");
                    school.setSimpleName("机械学院");
                    school.setCode("0003");
                    for (int j = 0; j < 3; j++) {
                        Major major = new Major();
                        switch (j) {
                            case 0:
                                major.setSchoolName("机械学院");
                                major.setMajorName("车辆工程");
                                major.setSimpleName("车辆");
                                major.setCode("0301");
                                majorList.add(major);
                                break;
                            case 1:
                                major.setSchoolName("机械学院");
                                major.setMajorName("工业工程");
                                major.setSimpleName("工业");
                                major.setCode("0302");
                                majorList.add(major);
                                break;
                            case 2:
                                major.setSchoolName("机械学院");
                                major.setMajorName("测控技术与仪器");
                                major.setSimpleName("测控");
                                major.setCode("0303");
                                majorList.add(major);
                                break;
                        }
                    }
                    break;
                case 3:
                    school.setSchoolName("电气工程学院");
                    school.setSimpleName("电气学院");
                    school.setCode("0004");
                    for (int j = 0; j < 2; j++) {
                        Major major = new Major();
                        switch (j) {
                            case 0:
                                major.setSchoolName("电气学院");
                                major.setMajorName("电气工程及其自动化");
                                major.setSimpleName("电气");
                                major.setCode("0401");
                                majorList.add(major);
                                break;
                            case 1:
                                major.setSchoolName("电气学院");
                                major.setMajorName("电子信息工程");
                                major.setSimpleName("电子");
                                major.setCode("0402");
                                majorList.add(major);
                                break;
                        }
                    }
                    break;
                case 4:
                    school.setSchoolName("人文学院");
                    school.setSimpleName("人文学院");
                    school.setCode("0005");
                    for (int j = 0; j < 3; j++) {
                        Major major = new Major();
                        switch (j) {
                            case 0:
                                major.setSchoolName("人文学院");
                                major.setMajorName("汉语言文学");
                                major.setSimpleName("汉语");
                                major.setCode("0501");
                                majorList.add(major);
                                break;
                            case 1:
                                major.setSchoolName("人文学院");
                                major.setMajorName("传播学");
                                major.setSimpleName("传播");
                                major.setCode("0502");
                                majorList.add(major);
                                break;
                            case 2:
                                major.setSchoolName("人文学院");
                                major.setMajorName("广告学");
                                major.setSimpleName("广告");
                                major.setCode("0503");
                                majorList.add(major);
                                break;
                        }
                    }
                    break;
            }
            schoolList.add(school);
        }

        if (!saved) {
            for (int i = 0; i < schoolList.size(); i++) {
                schoolList.get(i).save();
            }
            for (int i = 0; i < majorList.size(); i++) {
                majorList.get(i).save();
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("save", true);
            editor.apply();
        }

        List<String> schoolNameList = new ArrayList<>();
        schoolNameList.add("所有");
        schoolMajors.put("所有", null);
        List<String> allMajor = new ArrayList<>();
        for (int i = 0; i < schoolList.size(); i++) {
            String schoolName = schoolList.get(i).getSimpleName();
            schoolNameList.add(schoolName);
            List<String> majors = new ArrayList<>();
            majors.add("所有");
            for (int j = 0; j < majorList.size(); j++) {
                if (majorList.get(j).getSchoolName().equals(schoolName)) {
                    majors.add(majorList.get(j).getSimpleName());
                }
                if (i == 0)
                    allMajor.add(majorList.get(j).getSimpleName());
            }
            schoolMajors.put(schoolName, majors);
        }
        schoolMajors.put("所有", allMajor);
        List<String> includeAll = new ArrayList<>(schoolNameList);

        groupSpinnerSchool.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, includeAll));
        groupSpinnerMajor.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, schoolMajors.get(includeAll.get(0))));

        schoolNameList.remove("所有");
        updateMajorSelector(schoolList.get(0).getSimpleName());
        schoolSelector.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, schoolNameList));

        List<String> fields = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            List<String> operators = new ArrayList<>();
            switch (i) {
                case 0:
                    operators.add("=");
                    operators.add("!=");
                    operators.add("like");
                    fieldOperator.put("姓名", operators);
                    fields.add("姓名");
                    break;
                case 1:
                    operators.add("=");
                    operators.add("!=");
                    operators.add("like");
                    fieldOperator.put("性别", operators);
                    fields.add("性别");
                    break;
                case 2:
                    operators.add(">");
                    operators.add("=");
                    operators.add("<");
                    operators.add("!=");
                    operators.add(">=");
                    operators.add("<=");
                    operators.add("like");
                    fieldOperator.put("学号", operators);
                    fields.add("学号");
                    break;
                case 3:
                    operators.add("=");
                    operators.add("!=");
                    operators.add("like");
                    fieldOperator.put("学院", operators);
                    fields.add("学院");
                    break;
                case 4:
                    operators.add("=");
                    operators.add("!=");
                    operators.add("like");
                    fieldOperator.put("专业", operators);
                    fields.add("专业");
                    break;
                case 5:
                    operators.add(">");
                    operators.add("=");
                    operators.add("<");
                    operators.add("!=");
                    operators.add(">=");
                    operators.add("<=");
                    fieldOperator.put("年级", operators);
                    fields.add("年级");
                    break;
                case 6:
                    operators.add(">");
                    operators.add("=");
                    operators.add("<");
                    operators.add("!=");
                    operators.add(">=");
                    operators.add("<=");
                    fieldOperator.put("班级", operators);
                    fields.add("班级");
                    break;
                case 7:
                    operators.add(">");
                    operators.add("=");
                    operators.add("<");
                    operators.add("!=");
                    operators.add(">=");
                    operators.add("<=");
                    fieldOperator.put("入学日期", operators);
                    fields.add("入学日期");
                    break;
                case 8:
                    operators.add(">");
                    operators.add("=");
                    operators.add("<");
                    operators.add("!=");
                    operators.add(">=");
                    operators.add("<=");
                    fieldOperator.put("学制", operators);
                    fields.add("学制");
                    break;
                case 9:
                    operators.add("=");
                    operators.add("!=");
                    operators.add("like");
                    fieldOperator.put("类别", operators);
                    fields.add("类别");
                    break;
                case 10:
                    operators.add(">");
                    operators.add("=");
                    operators.add("<");
                    operators.add("!=");
                    operators.add(">=");
                    operators.add("<=");
                    fieldOperator.put("出生日期", operators);
                    fields.add("出生日期");
                    break;
            }
        }
        spinnerField.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fields));
        spinnerOperator.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fieldOperator.get(fields.get(0))));
        spinnerField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerOperator.setAdapter(new ArrayAdapter<>(StudentInfoActivity.this,
                        android.R.layout.simple_list_item_1, fieldOperator.get((String) spinnerField.getItemAtPosition(position))));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateMajorSelector(String schoolName) {
        majorSelector.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, schoolMajors.get(schoolName)));
    }

    private void selectXMajor(String key) {
        groupSpinnerMajor.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, schoolMajors.get(key)));
    }

    private void initFragment() {
        fragmentList = new ArrayList<>();
        FirstFragment firstFragment = FirstFragment.getInstance(studentList, true);
        FirstFragment secondFragment = FirstFragment.getInstance(studentList, false);

        fragmentList.add(firstFragment);
        fragmentList.add(secondFragment);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
    }

    private void updateTitle() {
        actionBar.setTitle("共有" + allCount + "项");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.input_interface, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exportExcel:
                exportStudentInfoExcel();
                break;
            case R.id.picTable:
                startActivity(new Intent(this,StudentPicTableActivity.class));
                break;
            case R.id.tableTech:
                printStudentInfo();
                //startActivity(new Intent(this, TableTechActivity.class));
                break;
            case R.id.search_menu:
                break;
            case R.id.sort_menu:
                break;
            case R.id.add_menu:
                if (bottomLayout.getVisibility() == View.VISIBLE)
                    break;
                addData = true;
                deleteBtn.setClickable(false);
                bottomLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.group_search:
                bottomGroupLayout.setVisibility(VISIBLE);
                break;
            case R.id.obscure_search:
                bottomMask.setVisibility(VISIBLE);
                break;
            case R.id.all_power_search:
                powerLayout.setVisibility(VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportStudentInfoExcel() {
        final EditText editText = (EditText) getLayoutInflater().inflate(R.layout.dialog_input, null, false);
        new AlertDialog.Builder(this).setView(editText).setNegativeButton("取消", null).setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(fileName)) {
                    saveExcelFile(fileName);
                } else {
                    Toast.makeText(StudentInfoActivity.this, "请输入文件名", Toast.LENGTH_SHORT).show();
                }
            }
        }).create().show();
    }

    private void saveExcelFile(String fileName) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            Toast.makeText(this, root, Toast.LENGTH_SHORT).show();
            File outFile = new File(root + "/" + fileName + ".xlsx");
            try {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {//如果没有授权
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                } else {
                    if (!outFile.exists()) {
                        outFile.createNewFile();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(outFile);
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                    for (int i = 0; i < studentList.size(); i++) {
                        String line = "";
                        Student student = studentList.get(i);
                        if (i == 0) {
                            line = "学生信息表\n";
                            bufferedOutputStream.write(line.getBytes());
                            line = "ID\t姓名\t性别\t学号\t学院\t专业\t年级\t班级\t入学日期\t学制\t类别\t出生日期\n";
                            bufferedOutputStream.write(line.getBytes());
                        }
                        line = "";
                        line += student.getId() + "\t";
                        line += student.getName() + "\t";
                        line += student.getSex() + "\t";
                        line += student.getStuNo() + "\t";
                        line += student.getSchoolName() + "\t";
                        line += student.getMajor() + "\t";
                        line += student.getGrade() + "\t";
                        line += student.getClassNo() + "\t";
                        line += new Date(student.getEnrollmentDate()).toString() + "\t";
                        line += student.getStudyTime() + "\t";
                        line += student.getCategory() + "\t";
                        line += new Date(student.getBirthDate()).toString() + "\n";
                        bufferedOutputStream.write(line.getBytes());
                    }
                    bufferedOutputStream.flush();
                    fileOutputStream.close();
                }
                Log.i(TAG, "outFile: " + outFile.length() + ",path: " + outFile.getPath() + ",isFile:" + outFile.isFile());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "saveExcelFile: ", e);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "saveExcelFile: ", e);
            }
        } else {
            Toast.makeText(this, "文件不可修改？", Toast.LENGTH_SHORT).show();
        }
    }

    private void printStudentInfo() {
        List<List<String>> studentInfo = new ArrayList<>();
        for (Student student : studentList) {
            List<String> strings = new ArrayList<>();
            strings.add(student.getName());
            strings.add(student.getSex());
            strings.add(student.getStuNo());
            strings.add(student.getSchoolName());
            strings.add(student.getMajor());
            strings.add(student.getGrade() + "");
            strings.add(student.getClassNo() + "");
            strings.add(new Date(student.getEnrollmentDate()).toString());
            strings.add(student.getStudyTime() + "");
            strings.add(student.getCategory());
            strings.add(new Date(student.getBirthDate()).toString());
            studentInfo.add(strings);
        }
        List<String> strings = new ArrayList<>();
        strings.add("合计(人数)");
        strings.add(studentInfo.size() + "");
        for (int j = 0; j < 9; j++)
            strings.add("");
        studentInfo.add(strings);
        Toast.makeText(this, "记录：" + studentInfo.size(), Toast.LENGTH_SHORT).show();
        List<String> columnTitle = Arrays.asList(new String[]{"姓名", "性别", "学号", "学院", "专业", "年级", "班级", "入学日期", "学制", "类别", "出生日期"});
        String tableTitle = "学生信息一览表";
        int[] columnScale = {2, 2, 3, 3, 2, 2, 2, 4, 1, 2, 3};
        int itemsPerPage = 8;
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        String jobName = "printBookRestore";
        printManager.print(jobName, new MyPrintDocumentAdapter(this, studentInfo, columnTitle, tableTitle, columnScale, itemsPerPage), null);
    }

    class MyOnClickListener implements View.OnClickListener {

        Context context;

        public MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.closeButton:
                    if (addData) {
                        new AlertDialog.Builder(context).setMessage("放弃添加？").setTitle("关闭窗口").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bottomLayout.setVisibility(GONE);
                                addData = false;
                                clearBottom();
                            }
                        }).create().show();
                    } else if (getOneStudent().equals(currStudent)) {
                        bottomLayout.setVisibility(GONE);
                        addData = false;
                        clearBottom();
                    } else {
                        new AlertDialog.Builder(context).setMessage("放弃本次修改？").setTitle("关闭窗口").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bottomLayout.setVisibility(GONE);
                                addData = false;
                                clearBottom();
                            }
                        }).create().show();
                    }
                    hideKeyboard();
                    break;
                case R.id.edit_grade:
                    Calendar calendar = Calendar.getInstance();
                    final NumberPicker numberPicker = new NumberPicker(context, null);
                    numberPicker.setMaxValue(calendar.get(Calendar.YEAR));
                    numberPicker.setMinValue(calendar.get(Calendar.YEAR) - 30);
                    numberPicker.setValue(calendar.get(Calendar.YEAR));
                    new AlertDialog.Builder(context).setView(numberPicker).setTitle("选择年级").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            gradeWra.getEditText().setText("" + numberPicker.getValue());
                        }
                    }).setNegativeButton("取消", null).create().show();
                    break;
                case group_grade_high:
                    Calendar calendar3 = Calendar.getInstance();
                    final NumberPicker numberPicker3 = new NumberPicker(context, null);
                    numberPicker3.setMaxValue(calendar3.get(Calendar.YEAR));
                    numberPicker3.setMinValue(calendar3.get(Calendar.YEAR) - 30);
                    numberPicker3.setValue(calendar3.get(Calendar.YEAR));
                    new AlertDialog.Builder(context).setView(numberPicker3).setTitle("选择年级").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            groupGradeHigh.setText("" + numberPicker3.getValue());
                        }
                    }).setNegativeButton("取消", null).create().show();
                    break;
                case group_grade_low:
                    Calendar calendar4 = Calendar.getInstance();
                    final NumberPicker numberPicker4 = new NumberPicker(context, null);
                    numberPicker4.setMaxValue(calendar4.get(Calendar.YEAR));
                    numberPicker4.setMinValue(calendar4.get(Calendar.YEAR) - 30);
                    numberPicker4.setValue(calendar4.get(Calendar.YEAR));
                    new AlertDialog.Builder(context).setView(numberPicker4).setTitle("选择年级").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            groupGradeLow.setText("" + numberPicker4.getValue());
                        }
                    }).setNegativeButton("取消", null).create().show();
                    break;
                case R.id.edit_enrollment_date:
                    final Calendar calendar1 = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context, 0, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Calendar calendar2 = Calendar.getInstance();
                            calendar2.set(year, month, dayOfMonth);
                            if (calendar1.compareTo(calendar2) >= 0) {
                                enrollmentDateWra.getEditText().setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                            } else {
                                Toast.makeText(context, "日期不合法", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.create();
                    datePickerDialog.show();
                    break;
                case R.id.group_birth_date_high:
                    final Calendar calendar5 = Calendar.getInstance();
                    DatePickerDialog datePickerDialog5 = new DatePickerDialog(context, 0, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Calendar calendar2 = Calendar.getInstance();
                            calendar2.set(year, month, dayOfMonth);
                            if (calendar5.compareTo(calendar2) >= 0) {
                                groupBirthHigh.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                            } else {
                                Toast.makeText(context, "日期不合法", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, calendar5.get(Calendar.YEAR), calendar5.get(Calendar.MONTH), calendar5.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog5.create();
                    datePickerDialog5.show();
                    break;
                case R.id.group_birth_date_low:
                    final Calendar calendar6 = Calendar.getInstance();
                    DatePickerDialog datePickerDialog6 = new DatePickerDialog(context, 0, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Calendar calendar2 = Calendar.getInstance();
                            calendar2.set(year, month, dayOfMonth);
                            if (calendar6.compareTo(calendar2) >= 0) {
                                groupBirthLow.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                            } else {
                                Toast.makeText(context, "日期不合法", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, calendar6.get(Calendar.YEAR), calendar6.get(Calendar.MONTH), calendar6.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog6.create();
                    datePickerDialog6.show();
                    break;
                case R.id.edit_birth_date:
                    final Calendar calendar2 = Calendar.getInstance();
                    DatePickerDialog datePickerDialog2 = new DatePickerDialog(context, 0, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Calendar calendar3 = Calendar.getInstance();
                            calendar3.set(year, month, dayOfMonth);
                            if (calendar2.compareTo(calendar3) >= 0) {
                                birthDateWra.getEditText().setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                            } else {
                                Toast.makeText(context, "日期不合法", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog2.create();
                    datePickerDialog2.show();
                    break;
                case R.id.deleteButton:
                    new AlertDialog.Builder(context).setTitle("删除学生").setMessage("学生：" + currStudent.getName() + "，学号：" + currStudent.getStuNo())
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(TAG, "删除前: " + studentList);
                                    DataSupport.deleteAll(Student.class, "id = ? and stuNo = ?", "" + currStudent.getId(), currStudent.getStuNo());
                                    studentList = DataSupport.findAll(Student.class);
                                    Log.i(TAG, "删除后: " + studentList);
                                    allCount = studentList.size();
                                    updateTitle();
                                    for (Fragment fragment : fragmentList) {
                                        ((FirstFragment) fragment).deleteItem(studentList);
                                    }
                                    bottomLayout.setVisibility(GONE);
                                    addData = false;
                                    clearBottom();
                                }
                            }).setNegativeButton("取消", null).create().show();
                    break;
                case R.id.saveButton:
                    String name = stuNameWra.getEditText().getText().toString().trim();
                    String sex = (String) sexSelector.getSelectedItem();
                    String stuNo = stuNoWra.getEditText().getText().toString().trim();
                    String school = (String) schoolSelector.getSelectedItem();
                    String major = (String) majorSelector.getSelectedItem();
                    String grade = gradeWra.getEditText().getText().toString().trim();
                    String classNo = classNoWra.getEditText().getText().toString().trim();
                    String studyTime = studyTimeWra.getEditText().getText().toString().trim();
                    String category = (String) categorySelector.getSelectedItem();
                    String enrollmentDate = enrollmentDateWra.getEditText().getText().toString().trim();
                    String birthDate = birthDateWra.getEditText().getText().toString().trim();
                    Log.i(TAG, "saveButton: " + "name:" + name + ",sex:" + sex + ",stuNo:" + stuNo + ",school:" + school + ",major:" + major + ",grade:" + grade
                            + ",classNo:" + classNo + ",studyTime:" + studyTime + ",category:" + category + ",enrollmentDate:" + enrollmentDate + ",birthDate:" + birthDate);
                    if (TextUtils.isEmpty(name)) {
                        stuNameWra.setError("填写姓名");
                        stuNameWra.getEditText().requestFocus();
                    } else if (TextUtils.isEmpty(stuNo)) {
                        stuNoWra.setError("填写学号");
                        stuNameWra.getEditText().requestFocus();
                    } else if (TextUtils.isEmpty(grade)) {
                        gradeWra.setError("选择年级");
                        gradeWra.getEditText().requestFocus();
                    } else if (TextUtils.isEmpty(classNo) || !TextUtils.isDigitsOnly(classNo)) {
                        classNoWra.setError("班级有误");
                        classNoWra.getEditText().requestFocus();
                    } else if (TextUtils.isEmpty(studyTime) || !TextUtils.isDigitsOnly(studyTime)) {
                        studyTimeWra.setError("学制有误");
                        studyTimeWra.getEditText().requestFocus();
                    } else if (TextUtils.isEmpty(enrollmentDate)) {
                        enrollmentDateWra.setError("选择日期");
                        enrollmentDateWra.getEditText().requestFocus();
                    } else if (TextUtils.isEmpty(birthDate)) {
                        birthDateWra.setError("选择日期");
                        birthDateWra.getEditText().requestFocus();
                    } else {
                        Student student = new Student();
                        student.setName(name);
                        student.setSex(sex);
                        student.setStuNo(stuNo);
                        student.setSchoolName(school);
                        student.setMajor(major);
                        student.setGrade(parseInt(grade));
                        student.setClassNo(parseInt(classNo));
                        student.setStudyTime(parseInt(studyTime));
                        student.setCategory(category);
                        student.setEnrollmentDate(Date.valueOf(enrollmentDate).getTime());
                        student.setBirthDate(Date.valueOf(birthDate).getTime());
                        student.setInfoRedundancy(student.mySetInfoRedundancy());
                        List<Student> tempStudents = select("*").where("stuNo = ?", stuNo).find(Student.class);
                        if (addData) {//添加学生
                            if (tempStudents != null && tempStudents.size() > 0) {
                                Toast.makeText(context, "该学号已存在", Toast.LENGTH_SHORT).show();
                                stuNoWra.getEditText().requestFocus();
                            } else {
                                student.save();
                                studentList = DataSupport.findAll(Student.class);
                                Log.i(TAG, "save: " + studentList);
                                allCount = studentList.size();
                                updateTitle();
                                ((FirstFragment) fragmentList.get(0)).updateData(studentList);
                                ((FirstFragment) fragmentList.get(1)).updateData(studentList);

                                bottomLayout.setVisibility(GONE);
                                addData = false;
                                clearBottom();
                            }
                        } else {//修改学生
                            if (tempStudents != null && tempStudents.size() > 0 && tempStudents.get(0).getId() != currStudent.getId()) {
                                Toast.makeText(context, "该学号已存在", Toast.LENGTH_SHORT).show();
                                stuNoWra.getEditText().requestFocus();
                            } else {
                                student.updateAll("id = ?", currStudent.getId() + "");
                                studentList = DataSupport.findAll(Student.class);
                                allCount = studentList.size();
                                updateTitle();
                                for (Fragment fragment : fragmentList) {
                                    ((FirstFragment) fragment).deleteItem(studentList);
                                    (((FirstFragment) fragment).recyclerView).scrollToPosition(currIndex);
                                }

                                bottomLayout.setVisibility(GONE);
                                addData = false;
                                clearBottom();
                            }
                        }
                    }
                    break;
                case R.id.clearButton:
                    if (addData) {
                        clearBottom();
                    } else {
                        //Toast.makeText(context, "currIndex："+currIndex, Toast.LENGTH_SHORT).show();
                        clickItem(currIndex);
                    }
                    break;
                case R.id.groupCloseButton:
                    bottomGroupLayout.setVisibility(GONE);
                    break;
                case R.id.resetButton:
                    resetBottomGroup();
                    break;
                case R.id.retrieveButton:
                    startRetrieve();
                    break;
            }
        }
    }

    private void startRetrieve() {
        String name = groupName.getText().toString().trim();
        String id = groupId.getText().toString().trim();
        String gradeLow = (groupGradeLow.getText().toString().trim());
        String gradeHigh = (groupGradeHigh.getText().toString().trim());
        String yearLimit = (groupYearLimit.getText().toString().trim());
        String classStr = (groupClass.getText().toString().trim());
        String birthLow = (groupBirthLow.getText().toString().trim());
        String birthHigh = (groupBirthHigh.getText().toString().trim());
        boolean isNameAllMatch = groupNameMatch.isChecked();
        boolean isIdAllMatch = groupIdMatch.isChecked();
        String school = (String) groupSpinnerSchool.getSelectedItem();
        String major = (String) groupSpinnerMajor.getSelectedItem();
        String sex = (String) groupSpinnerSex.getSelectedItem();
        String degree = (String) groupSpinnerDegree.getSelectedItem();

        StringBuilder stringBuilder = new StringBuilder();
        String[] params = new String[16];
        int count = 1;
        if (!TextUtils.isEmpty(name)) {
            if (isNameAllMatch)
                params[count++] = name;
            else params[count++] = "%" + name + "%";
            stringBuilder.append("name  like ? ");
        } else {
            stringBuilder.append("name  like ? ");
            params[count++] = "%";
        }

        if (!TextUtils.isEmpty(id)) {
            stringBuilder.append("and stuNo like ? ");
            if (isIdAllMatch)
                params[count++] = id;
            else params[count++] = "%" + id + "%";
        }

        if (!TextUtils.isEmpty(gradeLow)) {//年级下限
            int low = Integer.parseInt(gradeLow);
            if (!TextUtils.isEmpty(gradeHigh)) {

                int high = Integer.parseInt(gradeHigh);
                if (low > high) {//上下限
                    Toast.makeText(this, "年级区间错误", Toast.LENGTH_SHORT).show();
                    groupGradeHigh.requestFocus();
                    return;
                } else {
                    stringBuilder.append("and grade >= ? and grade <= ? ");
                    params[count++] = gradeLow;
                    params[count++] = gradeHigh;
                }
            } else {//下限
                stringBuilder.append("and grade >= ? ");
                params[count++] = gradeLow;
            }
        } else if (!TextUtils.isEmpty(gradeHigh)) {//年级上限
            stringBuilder.append("and grade <= ? ");
            params[count++] = gradeHigh;
        }

        if (!TextUtils.isEmpty(classStr)) {
            stringBuilder.append("and classNo = ? ");
            params[count++] = classStr;
        }

        if (!TextUtils.isEmpty(yearLimit)) {
            stringBuilder.append("and studyTime = ? ");
            params[count++] = yearLimit;
        }

        if (!TextUtils.isEmpty(birthLow)) {//出生下限
            long low = Date.valueOf(birthLow).getTime();
            if (!TextUtils.isEmpty(birthHigh)) {
                long high = Date.valueOf(birthHigh).getTime();
                if (low > high) {//上下限
                    Toast.makeText(this, "出生区间错误", Toast.LENGTH_SHORT).show();
                    groupGradeHigh.requestFocus();
                    return;
                } else {
                    stringBuilder.append("and birthDate >= ? and birthDate <= ? ");
                    params[count++] = low + "";
                    params[count++] = high + "";
                }
            } else {//下限
                stringBuilder.append("and birthDate >= ? ");
                params[count++] = low + "";
            }
        } else if (!TextUtils.isEmpty(birthHigh)) {//出生上限
            stringBuilder.append("and birthDate <= ? ");
            long high = Date.valueOf(birthHigh).getTime();
            params[count++] = high + "";
        }

        if (!school.equals("所有")) {
            stringBuilder.append("and schoolName like ? ");
            params[count++] = school;
            if (!major.equals("所有")) {
                stringBuilder.append("and majorName like ? ");
                params[count++] = major;
            }
        }

        if (!sex.equals("所有")) {
            stringBuilder.append("and sex like ? ");
            params[count++] = sex;
        }

        if (!degree.equals("所有")) {
            stringBuilder.append("and category like ? ");
            params[count++] = degree;
        }
        params[0] = stringBuilder.toString();
        String[] realParams = Arrays.copyOfRange(params, 0, count);
        List<Student> retrieveStudents = DataSupport.select("*").where(realParams).find(Student.class);
        studentList = retrieveStudents;
        ((FirstFragment) fragmentList.get(0)).updateData(studentList);
        ((FirstFragment) fragmentList.get(1)).updateData(studentList);
        allCount = retrieveStudents.size();
        updateTitle();
        bottomGroupLayout.setVisibility(GONE);
    }

    private void resetBottomGroup() {
        groupName.setText("");
        groupId.setText("");
        groupClass.setText("");
        groupGradeLow.setText("");
        groupGradeHigh.setText("");
        groupYearLimit.setText("");
        groupBirthLow.setText("");
        groupBirthHigh.setText("");
        groupNameMatch.setChecked(false);
        groupIdMatch.setChecked(false);
        groupSpinnerSchool.setSelection(0);
        groupSpinnerMajor.setSelection(0);
        groupSpinnerSex.setSelection(0);
        groupSpinnerDegree.setSelection(0);

    }


    private void clearBottom() {
        stuNameWra.getEditText().setText("");
        stuNameWra.setErrorEnabled(false);
        //stuNameWra.setError("");
        sexSelector.setSelection(0);
        stuNoWra.getEditText().setText("");
        stuNoWra.setErrorEnabled(false);
        schoolSelector.setSelection(0);
        majorSelector.setSelection(0);
        gradeWra.getEditText().setText("");
        gradeWra.setErrorEnabled(false);
        classNoWra.getEditText().setText("");
        classNoWra.setErrorEnabled(false);
        studyTimeWra.getEditText().setText("");
        studyTimeWra.setErrorEnabled(false);
        categorySelector.setSelection(0);
        enrollmentDateWra.getEditText().setText("");
        enrollmentDateWra.setErrorEnabled(false);
        birthDateWra.getEditText().setText("");
        birthDateWra.setErrorEnabled(false);
    }

    private Student getOneStudent() {
        String name = stuNameWra.getEditText().getText().toString().trim();
        String sex = (String) sexSelector.getSelectedItem();
        String stuNo = stuNoWra.getEditText().getText().toString().trim();
        String school = (String) schoolSelector.getSelectedItem();
        String major = (String) majorSelector.getSelectedItem();
        String grade = gradeWra.getEditText().getText().toString().trim();
        String classNo = classNoWra.getEditText().getText().toString().trim();
        String studyTime = studyTimeWra.getEditText().getText().toString().trim();
        String category = (String) categorySelector.getSelectedItem();
        String enrollmentDate = enrollmentDateWra.getEditText().getText().toString().trim();
        String birthDate = birthDateWra.getEditText().getText().toString().trim();
        Student student = new Student();
        student.setId(currStudent.getId());
        student.setName(name);
        student.setSex(sex);
        student.setStuNo(stuNo);
        student.setSchoolName(school);
        student.setMajor(major);
        student.setGrade(parseInt(grade));
        student.setClassNo(parseInt(classNo));
        student.setStudyTime(parseInt(studyTime));
        student.setCategory(category);
        student.setEnrollmentDate(Date.valueOf(enrollmentDate).getTime());
        student.setBirthDate(Date.valueOf(birthDate).getTime());

        return student;
    }


    public void clickItem(int index) {
        currIndex = index;
        Log.i(TAG, "clickItem: index = " + index);
        currStudent = studentList.get(index);
        addData = false;
        deleteBtn.setClickable(true);
        bottomLayout.setVisibility(VISIBLE);
        stuNameWra.getEditText().setText(currStudent.getName());
        sexSelector.setSelection(currStudent.getSex().equals("男") ? 0 : 1);
        stuNoWra.getEditText().setText(currStudent.getStuNo());
        int position = 0;
        for (int i = 0; i < schoolSelector.getCount(); i++) {
            if (((String) schoolSelector.getItemAtPosition(i)).equals(currStudent.getSchoolName())) {
                position = i;
                break;
            }
        }
        schoolSelector.setSelection(position);
        updateMajorSelector(schoolList.get(position).getSimpleName());

        position = 0;
        Log.i(TAG, "getMajor: " + currStudent.getMajor());
        for (int i = 0; i < majorSelector.getCount(); i++) {
            if (((String) majorSelector.getItemAtPosition(i)).equals(currStudent.getMajor())) {
                position = i;
                majorSelector.setSelection(position);
                Log.i(TAG, "position: " + position);
                Log.i(TAG, "majorSelector,setSelection: " + majorSelector.getSelectedItem());
                break;
            }
        }
        majorSelector.setSelection(position);

        gradeWra.getEditText().setText("" + currStudent.getGrade());
        classNoWra.getEditText().setText("" + currStudent.getClassNo());
        studyTimeWra.getEditText().setText("" + currStudent.getStudyTime());

        String[] types = getResources().getStringArray(R.array.category);
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(currStudent.getCategory())) {
                position = i;
                break;
            }
        }
        categorySelector.setSelection(position);
        enrollmentDateWra.getEditText().setText(new Date(currStudent.getEnrollmentDate()).toString());
        birthDateWra.getEditText().setText(new Date(currStudent.getBirthDate()).toString());
    }

    //隐藏软键盘
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void maskRetrieve(View v) {
        String content = (editRetrieve.getText().toString().trim());
        content = content.replaceAll(" ", "");
        List<Student> students;
        if (!TextUtils.isEmpty(content))
            students = DataSupport.select("*").where("infoRedundancy like ?", "%" + content + "%").find(Student.class);
        else students = DataSupport.findAll(Student.class);
        studentList = students;
        allCount = students.size();
        updateTitle();
        bottomMask.setVisibility(GONE);
        ((FirstFragment) fragmentList.get(0)).updateData(studentList);
        ((FirstFragment) fragmentList.get(1)).updateData(studentList);
        Log.i(TAG, "maskRetrieve: " + studentList);
    }

    public void maskCloseWin(View v) {
        bottomMask.setVisibility(GONE);
        editRetrieve.setText("");
        hideKeyboard();
    }

    public void maskClear(View v) {
        editRetrieve.setText("");
    }

    public void addCondition(View v) {
        String logicOption = (String) spinnerLogicOption.getSelectedItem();
        String field = (String) spinnerField.getSelectedItem();
        String operator = (String) spinnerOperator.getSelectedItem();
        String value = editValue.getText().toString().trim();

        if (!TextUtils.isEmpty(value)) {
            if (powerConditionAdapter.checkedList.size() == 0) {
                logicOption = "";
            }
            powerConditionAdapter.addConditionItem(logicOption, field, operator, value);
        } else {
            Toast.makeText(this, "请输入值", Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteCondition(View v) {
        if (powerConditionAdapter.count > 0)
            new AlertDialog.Builder(this).setMessage("删除所选条件？").setNegativeButton("取消", null).
                    setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            powerConditionAdapter.deleteCheckedItem();
                        }
                    }).create().show();
    }

    public void powerRetrieve(View v) {
        String sql = "";
        boolean firstRow = true;
        for (int i = 0; i < powerConditionAdapter.conditionStrings.size(); i++) {
            if (powerConditionAdapter.checkedList.get(i)) {
                List<String> condition = powerConditionAdapter.conditionStrings.get(i);
                String temp = "";
                if (firstRow) {
                    firstRow = false;
                } else {
                    temp += condition.get(0);
                }
                switch (condition.get(1)) {
                    case "姓名":
                        temp += " name " + condition.get(2) + " '" + condition.get(3) + "' ";
                        break;
                    case "性别":
                        temp += " sex " + condition.get(2) + " '" + condition.get(3) + "' ";
                        break;
                    case "学号":
                        temp += " stuNo " + condition.get(2) + " '" + condition.get(3) + "' ";
                        break;
                    case "学院":
                        temp += " schoolName " + condition.get(2) + " '" + condition.get(3) + "' ";
                        break;
                    case "专业":
                        temp += " majorName " + condition.get(2) + " '" + condition.get(3) + "' ";
                        break;
                    case "年级":
                        temp += " grade " + condition.get(2) + " '" + condition.get(3) + "' ";
                        break;
                    case "班级":
                        temp += " classNo " + condition.get(2) + " '" + condition.get(3) + "' ";
                        break;
                    case "入学日期":
                        temp += " enrollmentDate " + condition.get(2) + " '" + condition.get(3) + "' ";
                        break;
                    case "学制":
                        temp += " studyTime " + condition.get(2) + " '" + condition.get(3) + "' ";
                        break;
                    case "类别":
                        temp += " category " + condition.get(2) + " '" + condition.get(3) + "' ";
                        break;
                    case "出生日期":
                        temp += " birthDate " + condition.get(2) + " '" + condition.get(3) + "' ";
                        break;
                }
                sql += temp;
            }
        }
        Log.i(TAG, "powerRetrieve: " + sql);
        List<Student> students = DataSupport.select("*").where(sql).find(Student.class);
        studentList = students;
        allCount = students.size();
        updateTitle();
        powerLayout.setVisibility(GONE);
        ((FirstFragment) fragmentList.get(0)).updateData(students);
        ((FirstFragment) fragmentList.get(1)).updateData(students);
    }

    public void powerCloseWin(View v) {
        powerLayout.setVisibility(GONE);
    }

    private LinearLayout bottomGroupLayout;
    private EditText groupName, groupId, groupClass, groupGradeLow, groupGradeHigh, groupYearLimit, groupBirthLow, groupBirthHigh;
    private CheckBox groupNameMatch, groupIdMatch;
    private Spinner groupSpinnerSchool, groupSpinnerMajor, groupSpinnerSex, groupSpinnerDegree;
    private Button groupRetrieveBtn, groupResetBtn;
    private TextView groupCloseButton;
    private Map<String, List<String>> schoolMajors = new HashMap<>();

    private LinearLayout bottomMask;
    private EditText editRetrieve;

    private LinearLayout powerLayout;
    private EditText editValue;
    private Spinner spinnerLogicOption, spinnerField, spinnerOperator;
    public RecyclerView recyclerView;
    private PowerConditionAdapter powerConditionAdapter;

    private Map<String, List<String>> fieldOperator = new HashMap<>();
}
