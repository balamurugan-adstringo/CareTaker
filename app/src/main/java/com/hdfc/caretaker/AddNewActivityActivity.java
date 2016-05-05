package com.hdfc.caretaker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hdfc.adapters.ActivityServicesAdapter;
import com.hdfc.app42service.StorageService;
import com.hdfc.config.Config;
import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Utils;
import com.hdfc.models.CategoryServiceModel;
import com.hdfc.models.FieldModel;
import com.hdfc.models.MilestoneModel;
import com.hdfc.models.ServiceModel;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddNewActivityActivity extends AppCompatActivity {

    public static List<ServiceModel> dependentServiceModels = new ArrayList<>();
    public static List<ServiceModel> selectedDependentServiceModels = new ArrayList<>();
    private static ProgressDialog progressDialog;
    private static Button buttonContinue;
    private static ArrayList<String> strServcieIds = new ArrayList<>();
    private static ArrayList<CategoryServiceModel> categoryServiceModels = new ArrayList<>();
    private static ArrayList<String> strServiceCategoryNames = new ArrayList<>();
    private Utils utils;

    private List<String> listDataHeader = new ArrayList<>();
    private HashMap<String, List<ServiceModel>> listDataChild = new HashMap<>();

    private ExpandableListView listView;
    private ActivityServicesAdapter activityServicesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        utils = new Utils(AddNewActivityActivity.this);
        listView = (ExpandableListView) findViewById(R.id.listView1);
        TextView textViewEmpty = (TextView) findViewById(android.R.id.empty);
        buttonContinue = (Button) findViewById(R.id.buttonContinue);
        ImageButton imageButtonBuyServices = (ImageButton) findViewById(R.id.imageButtonBuyServices);

        progressDialog = new ProgressDialog(AddNewActivityActivity.this);

        strServcieIds.clear();
        categoryServiceModels.clear();
        strServiceCategoryNames.clear();

        if (listView != null) {
            listView.setEmptyView(textViewEmpty);

            activityServicesAdapter = new ActivityServicesAdapter(this, listDataChild,
                    listDataHeader);
            listView.setAdapter(activityServicesAdapter);

            listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {

                    RadioButton checkBox = (RadioButton) v.findViewById(R.id.checkBoxService);
                    ServiceModel serviceModel = (ServiceModel) checkBox.getTag();

                    if (checkBox.isChecked()) {
                        checkBox.setChecked(false);
                        checkBox.setButtonDrawable(getResources().
                                getDrawable(R.mipmap.check_off));
                    } else {
                        checkBox.setChecked(true);
                        checkBox.setButtonDrawable(getResources().
                                getDrawable(R.mipmap.check_on));
                    }

                    return false;
                }
            });
        }

        /*if (listView != null) {
            listView.setAdapter(activityServicesAdapter);
            listView.setEmptyView(textViewEmpty);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    int count = parent.getChildCount();
                    View v;

                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBoxService);
                    ServiceModel dependentServiceModel = (ServiceModel) checkBox.getTag();

                    if (checkBox.isChecked()) {
                        selectedDependentServiceModels.remove(dependentServiceModel);
                        checkBox.setChecked(false);
                    } else {

                        for (int i = 0; i < count; i++) {
                            if (i != position) {
                                v = parent.getChildAt(i);
                                CheckBox checkBoxAll = (CheckBox) v.findViewById(R.id.checkBoxService);
                                checkBoxAll.setChecked(false);
                            }
                        }
                        selectedDependentServiceModels.clear();

                        selectedDependentServiceModels.add(dependentServiceModel);
                        checkBox.setChecked(true);
                    }

                }
            });
        }*/

        Button cancelButton = (Button) findViewById(R.id.buttonCancel);
        if (cancelButton != null) {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBack();
                }
            });
        }

        if (imageButtonBuyServices != null) {
            imageButtonBuyServices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddNewActivityActivity.this, AdditionalServicesActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    public void goBack() {
        Intent newIntent = new Intent(AddNewActivityActivity.this, DashboardActivity.class);
        startActivity(newIntent);
        finish();
    }

    public void refreshAdapter() {

        Utils.log(" 4 ", " IN ");

        try {
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            Utils.log(" 5 ", " IN ");

            if (listView != null) {

                Utils.log(" 6 ", " IN ");

                listDataHeader.clear();
                listDataChild.clear();

                Utils.log(String.valueOf(categoryServiceModels.size()), " IN 4");

                for (CategoryServiceModel categoryServiceModel : categoryServiceModels) {

                    Utils.log(categoryServiceModel.getStrCategoryName(), " CAT ");
                    Utils.log(String.valueOf(categoryServiceModel.getServiceModels().size()), " CAT S");

                    listDataHeader.add(categoryServiceModel.getStrCategoryName());
                    listDataChild.put(categoryServiceModel.getStrCategoryName(),
                            categoryServiceModel.getServiceModels());
                }

                activityServicesAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addNewActivityStep2(View v) {

        if (selectedDependentServiceModels.size() > 0) {

            ServiceModel serviceModel = selectedDependentServiceModels.get(0);

            if((serviceModel.getiUnit()-serviceModel.getiUnitUsed())>0) {

                Intent newIntent = new Intent(AddNewActivityActivity.this, AddNewActivityStep2Activity.class);
                startActivity(newIntent);
                finish();
            } else utils.toast(2, 2, getResources().getString(R.string.error_service_zero));

        } else utils.toast(2, 2, getResources().getString(R.string.error_service));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //finish();
        goBack();
    }

    @Override
    protected void onResume() {
        super.onResume();

        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageService storageService = new StorageService(AddNewActivityActivity.this);

        storageService.findDocsByKeyValue(Config.collectionServiceCustomer, "customer_id",
                Config.customerModel.getStrCustomerID(),
                new AsyncApp42ServiceApi.App42StorageServiceListener() {

                    @Override
                    public void onDocumentInserted(Storage response) {
                    }

                    @Override
                    public void onUpdateDocSuccess(Storage response) {
                    }

                    @Override
                    public void onFindDocSuccess(Storage storage) {

                        try {
                            if (storage != null) {

                                Utils.log(storage.toString(), "RESP");

                                ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();

                                for (int i = 0; i < jsonDocList.size(); i++) {

                                    Storage.JSONDocument jsonDocument = jsonDocList.get(i);
                                    String strDocumentId = jsonDocument.getDocId();
                                    String strServices = jsonDocument.getJsonDoc();

                                    try {
                                        JSONObject jsonObjectServcies = new JSONObject(strServices);
                                        if (jsonObjectServcies.has("unit"))
                                            createActivityServiceModel(strDocumentId,
                                                    jsonObjectServcies);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } else {
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }

                            refreshAdapter();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onInsertionFailed(App42Exception ex) {
                    }

                    @Override
                    public void onFindDocFailed(App42Exception ex) {
                        try {
                            refreshAdapter();
                            if (ex != null) {
                                JSONObject jsonObject = new JSONObject(ex.getMessage());
                                JSONObject jsonObjectError = jsonObject.getJSONObject("app42Fault");
                                String strMess = jsonObjectError.getString("details");

                                utils.toast(2, 2, strMess);
                            } else {
                                utils.toast(2, 2, getString(R.string.warning_internet));
                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onUpdateDocFailed(App42Exception ex) {
                    }
                });

    }

    public void createActivityServiceModel(String strDocumentId, JSONObject jsonObject) {

        try {
            ServiceModel serviceModel = new ServiceModel();

            serviceModel.setDoubleCost(jsonObject.getDouble("cost"));
            serviceModel.setStrServiceName(jsonObject.getString("service_name"));
            serviceModel.setiServiceNo(jsonObject.getInt("service_no"));
            serviceModel.setStrCategoryName(jsonObject.getString("category_name"));
            serviceModel.setiUnit(jsonObject.getInt("unit"));
            serviceModel.setiUnitUsed(jsonObject.getInt("unit_consumed"));
            serviceModel.setStrServiceType(jsonObject.getString("service_type"));

            if (jsonObject.has("milestones")) {

                JSONArray jsonArrayMilestones = jsonObject.
                        getJSONArray("milestones");

                for (int k = 0; k < jsonArrayMilestones.length(); k++) {

                    JSONObject jsonObjectMilestone =
                            jsonArrayMilestones.getJSONObject(k);

                    MilestoneModel milestoneModel = new MilestoneModel();

                    milestoneModel.setiMilestoneId(jsonObjectMilestone.getInt("id"));
                    milestoneModel.setStrMilestoneStatus(jsonObjectMilestone.getString("status"));
                    milestoneModel.setStrMilestoneName(jsonObjectMilestone.getString("name"));
                    milestoneModel.setStrMilestoneDate(jsonObjectMilestone.getString("date"));

                    //
                    if (jsonObjectMilestone.has("fields")) {

                        JSONArray jsonArrayFields = jsonObjectMilestone.
                                getJSONArray("fields");

                        for (int l = 0; l < jsonArrayFields.length(); l++) {

                            JSONObject jsonObjectField =
                                    jsonArrayFields.getJSONObject(l);

                            FieldModel fieldModel = new FieldModel();

                            fieldModel.setiFieldID(jsonObjectField.getInt("id"));

                            if (jsonObjectField.has("hide"))
                                fieldModel.setFieldView(jsonObjectField.getBoolean("hide"));

                            fieldModel.setFieldRequired(jsonObjectField.getBoolean("required"));
                            fieldModel.setStrFieldData(jsonObjectField.getString("data"));
                            fieldModel.setStrFieldLabel(jsonObjectField.getString("label"));
                            fieldModel.setStrFieldType(jsonObjectField.getString("type"));

                            if (jsonObjectField.has("values")) {

                                fieldModel.setStrFieldValues(utils.jsonToStringArray(jsonObjectField.
                                        getJSONArray("values")));
                            }

                            if (jsonObjectField.has("child")) {

                                fieldModel.setChild(jsonObjectField.getBoolean("child"));

                                if (jsonObjectField.has("child_type"))
                                    fieldModel.setStrChildType(utils.jsonToStringArray(jsonObjectField.
                                            getJSONArray("child_type")));

                                if (jsonObjectField.has("child_value"))
                                    fieldModel.setStrChildValue(utils.jsonToStringArray(jsonObjectField.
                                            getJSONArray("child_value")));

                                if (jsonObjectField.has("child_condition"))
                                    fieldModel.setStrChildCondition(utils.jsonToStringArray(jsonObjectField.
                                            getJSONArray("child_condition")));

                                if (jsonObjectField.has("child_field"))
                                    fieldModel.setiChildfieldID(utils.jsonToIntArray(jsonObjectField.
                                            getJSONArray("child_field")));
                            }

                            milestoneModel.setFieldModel(fieldModel);
                        }
                    }

                    serviceModel.setMilestoneModels(milestoneModel);
                }
            }

            serviceModel.setStrServiceId(strDocumentId);

            if (!strServcieIds.contains(strDocumentId)) {
                strServcieIds.add(strDocumentId);

                Utils.log(" 1 ", " IN ");

                if (!strServiceCategoryNames.contains(jsonObject.getString("category_name"))) {
                    strServiceCategoryNames.add(jsonObject.getString("category_name"));

                    Utils.log(" 2 ", " IN ");

                    CategoryServiceModel categoryServiceModel = new CategoryServiceModel();
                    categoryServiceModel.setStrCategoryName(jsonObject.getString("category_name"));
                    categoryServiceModel.setServiceModels(serviceModel);

                    categoryServiceModels.add(categoryServiceModel);
                } else {
                    int iPosition = strServiceCategoryNames.indexOf(jsonObject.getString("category_name"));

                    Utils.log(" 3 ", " IN ");
                    categoryServiceModels.get(iPosition).setServiceModels(serviceModel);
                }
                //
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
