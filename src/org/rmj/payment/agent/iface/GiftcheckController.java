/**
 * Payment Main Form Controller Class
 *
 * @author Michael Cuison
 * @since August 20, 2018
 */

package org.rmj.payment.agent.iface;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.cas.parameter.agent.XMAffiliatedCompany;
import org.rmj.payment.pojo.UnitGCPaymentTrans;

public class GiftcheckController implements Initializable {

    @FXML
    private AnchorPane acMain;
    @FXML
    private Button cmdCancel;
    @FXML
    private Button cmdOkay;
    @FXML
    private Button cmdAdd;
    @FXML
    private TextField txtCompnyCd;
    @FXML
    private TextField txtValidity;
    @FXML
    private TextArea txtRemarksx;
    @FXML
    private TextField txtAmountxx;
    @FXML
    private TableView table;
    @FXML
    private TextField txtReferNox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (poData == null){
            ShowMessageFX.Warning("Gift check card data is not set.", pxeModuleName, "Please inform MIS Department.");
            System.exit(0);
        }
        
        cmdOkay.setOnAction(this::cmdButton_Click);
        cmdCancel.setOnAction(this::cmdButton_Click);
        cmdAdd.setOnAction(this::cmdButton_Click);
       
        txtCompnyCd.focusedProperty().addListener(txtField_Focus);
        txtReferNox.focusedProperty().addListener(txtField_Focus);
        txtValidity.focusedProperty().addListener(txtField_Focus);
        txtAmountxx.focusedProperty().addListener(txtField_Focus);
        txtRemarksx.focusedProperty().addListener(txtArea_Focus);
        
        txtCompnyCd.setOnKeyPressed(this::txtField_KeyPressed);
        txtReferNox.setOnKeyPressed(this::txtField_KeyPressed);
        txtValidity.setOnKeyPressed(this::txtField_KeyPressed);
        txtAmountxx.setOnKeyPressed(this::txtField_KeyPressed);
        txtRemarksx.setOnKeyPressed(this::txtFieldArea_KeyPressed);
        
        loadData();
        
        pbLoaded = true;
    }
    
    private boolean isEntryOK(){
        if (poData.getCompnyCd()== null || poData.getCompnyCd().equals("")){
            ShowMessageFX.Warning(getStage(), "Invalid affiliated company detected.", pxeModuleName, "Please verify your entry.");
            txtCompnyCd.requestFocus();
            return false;
        }
        
        if (poData.getReferNox()== null || poData.getReferNox().equals("")){
            ShowMessageFX.Warning(getStage(), "Invalid control number detected.", pxeModuleName, "Please verify your entry.");
            txtReferNox.requestFocus();
            return false;
        }
        
        if (poData.getValidity()== null || poData.getValidity() == CommonUtils.toDate(pxeDateDefault)){
            ShowMessageFX.Warning(getStage(), "Invalid validity detected.", pxeModuleName, "Please verify your entry.");
            txtValidity.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
        
        switch (lsButton.toLowerCase()){
            case "cmdokay":
                if (!isEntryOK()) return;
                
                pbCancelled = false;
                unloadScene(event);
                break;
            case "cmdcancel":
                pbCancelled = true;
                unloadScene(event);
                break;
            case "cmdadd":
                break;
            default:
                ShowMessageFX.Information(getStage(), "Button " + lsButton + " is not initialized.", pxeModuleName, "Please inform MIS department.");
        }
    }
    
    private void txtField_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField)event.getSource();
        String lsValue = txtField.getText();
        String [] lasSplit;
        
        switch(txtField.getId().toLowerCase()){
            case "txtcompnycd":
                switch (event.getCode()){
                    case F3:
                        XMAffiliatedCompany loCompany = new XMAffiliatedCompany(poGRider, poGRider.getBranchCode(), true);
                        JSONObject loJSON = loCompany.searchAffiliatedCompany(lsValue, false);
                        
                        if (loJSON != null){
                            poData.setCompnyCd((String) loJSON.get("sCompnyCd"));
                            txtField.setText((String) loJSON.get("sCompnyNm"));
                            CommonUtils.SetNextFocus(txtField);
                        } else poData.setCompnyCd("");
                }
        }
        
             
        if (null != event.getCode()){
            switch (event.getCode()) {
                case ENTER:
                case DOWN:
                    CommonUtils.SetNextFocus(txtField);
                    break;
                case UP:
                    CommonUtils.SetPreviousFocus(txtField);
                    break;
                default:
                    break;
            }
        }
    }
    
    private void loadData(){
        loadCompany();
        txtReferNox.setText(poData.getReferNox());
        txtValidity.setText(poData.getValidity()== null ? "" : CommonUtils.xsDateMedium(poData.getValidity()));
        txtAmountxx.setText(CommonUtils.NumberFormat((Double) poData.getAmountxx(), "#,##0.00"));
        txtRemarksx.setText(poData.getRemarksx());
    }
    
    private void txtFieldArea_KeyPressed(KeyEvent event) {
        TextArea txtArea = (TextArea)event.getSource();
             
        if (null != event.getCode())switch (event.getCode()) {
            case ENTER:
            case DOWN:
                event.consume();
                CommonUtils.SetNextFocus(txtArea);
                break;
            case UP:
                CommonUtils.SetPreviousFocus(txtArea);
                break;
            default:
                break;
        } 
    }
    
    private void loadCompany(){
        if (!poData.getCompnyCd().equals("")){
            XMAffiliatedCompany loCompany = new XMAffiliatedCompany(poGRider, poGRider.getBranchCode(), true);
            JSONObject loJSON = loCompany.searchAffiliatedCompany(poData.getCompnyCd(), true);

            if (loJSON != null){
                poData.setCompnyCd((String) loJSON.get("sCompnyCd"));
                txtCompnyCd.setText((String) loJSON.get("sCompnyNm"));
            }  else txtCompnyCd.setText("");
        } else txtCompnyCd.setText("");
        
    }
        
    private void unloadScene(ActionEvent event){
        Node source = (Node)  event.getSource(); 
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
    
    private Stage getStage(){
        return (Stage) acMain.getScene().getWindow();
    }
    
    @Override
    public String toString() {
        return pxeModuleName;
    }

    public void setGRider(GRider foGRider){
        poGRider = foGRider;
    }

    public void setData(UnitGCPaymentTrans foData){
        poData = foData;
    }
    
    public UnitGCPaymentTrans getData(){
        return poData;
    }
    
    public boolean isCancelled(){return pbCancelled;}
    private UnitGCPaymentTrans poData;
    private GRider poGRider;
    
    private final String pxeModuleName = "org.rmj.payment.agent.controller.GiftcheckController";
    private final String pxeDateFormat = "yyyy-MM-dd";
    private final String pxeDateDefault = "1900-01-01";
    private boolean pbCancelled = true;
    private boolean pbLoaded = false;
    
    final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{
        if (!pbLoaded) return;
        
        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        String lsTxtNm = txtField.getId();
        String lsValue = txtField.getText();
        
        double lnValue = 0.0;
        
        if (lsValue == null) return;
            
        if(!nv){ /*Lost Focus*/
            switch (lsTxtNm.toLowerCase()){
                case "txtcompnycd": break;
                case "txtreferno": poData.setReferNox(lsValue); break;
                case "txtvalidity":
                    if (CommonUtils.isDate(lsValue, pxeDateFormat)){
                        poData.setValidity(CommonUtils.toDate(lsValue));
                    } else{
                        ShowMessageFX.Warning(getStage(), "Invalid validity date.", pxeModuleName, "Date format must be yyyy-MM-dd (e.g. 1991-07-07)");
                        poData.setValidity(null);
                        txtField.requestFocus();
                        txtField.requestFocus();
                        return;
                    }
                    
                    txtField.setText(poData.getValidity()== null ? "" : CommonUtils.xsDateMedium(poData.getValidity()));
                    break;
                case "txtamountxx":
                    try {
                        lnValue = Double.parseDouble(lsValue);
                    } catch (Exception e) {
                        ShowMessageFX.Warning(getStage(), "Invalid amount format.", pxeModuleName, "Please verify your entry.");
                        return;
                    }
                    
                    poData.setAmountxx(lnValue);
                    poData.setAmtPaidx(lnValue);
                    txtField.setText(CommonUtils.NumberFormat(lnValue, "###0.00"));
                    break;
                default:
                    ShowMessageFX.Information(getStage(), "Textfield " + lsTxtNm + " is not initialized.", pxeModuleName, "Please inform MIS department.");
            }
        } else
            if (lsTxtNm.toLowerCase().equals("txtvalidity")){
                txtField.setText(poData.getValidity()== null ? "" : CommonUtils.xsDateShort(poData.getValidity()));
            } else txtField.selectAll();
    };
    
    final ChangeListener<? super Boolean> txtArea_Focus = (o,ov,nv)->{
        if (!pbLoaded) return;
        
        TextArea txtField = (TextArea)((ReadOnlyBooleanPropertyBase)o).getBean();
        String lsTxtNm = txtField.getId();
        String lsValue = txtField.getText();
        
        if (lsValue == null) return;
        
        if(!nv){ /*Lost Focus*/            
            switch (lsTxtNm.toLowerCase()){
                case "txtremarksx": poData.setRemarksx(lsValue); break;
            }
        }
    };  
}
