/**
 * Payment Main Form Controller Class
 *
 * @author Michael Cuison
 * @since August 20, 2018
 */
package org.rmj.payment.agent.iface;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
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
import org.rmj.appdriver.MiscUtil;
import org.rmj.appdriver.SQLUtil;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.cas.parameter.agent.XMBank;
import org.rmj.payment.pojo.UnitCheckPaymentTrans;

public class CheckController implements Initializable {

    @FXML
    private AnchorPane acMain;
    @FXML
    private Button cmdCancel;
    @FXML
    private Button cmdOkay;
    @FXML
    private Button cmdAdd;
    @FXML
    private TextField txtBankCode;
    @FXML
    private TextField txtCheckNox;
    @FXML
    private TextField txtCheckDte;
    @FXML
    private TextArea txtRemarksx;
    @FXML
    private TextField txtAmountxx;
    @FXML
    private TableView<?> table;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
         if (poData == null){
            ShowMessageFX.Warning("Check data is not set.", pxeModuleName, "Please inform MIS Department.");
            System.exit(0);
        }
        
        cmdOkay.setOnAction(this::cmdButton_Click);
        cmdCancel.setOnAction(this::cmdButton_Click);
        cmdAdd.setOnAction(this::cmdButton_Click);
       
        txtBankCode.focusedProperty().addListener(txtField_Focus);
        txtCheckNox.focusedProperty().addListener(txtField_Focus);
        txtCheckDte.focusedProperty().addListener(txtField_Focus);
        txtAmountxx.focusedProperty().addListener(txtField_Focus);
        txtRemarksx.focusedProperty().addListener(txtArea_Focus);
        
        txtBankCode.setOnKeyPressed(this::txtField_KeyPressed);
        txtCheckNox.setOnKeyPressed(this::txtField_KeyPressed);
        txtCheckDte.setOnKeyPressed(this::txtField_KeyPressed);
        txtAmountxx.setOnKeyPressed(this::txtField_KeyPressed);
        txtRemarksx.setOnKeyPressed(this::txtFieldArea_KeyPressed);
        
        loadData();
        
        pbLoaded = true;
    }
    
    private boolean isEntryOK(){
        if (poData.getBankCode() == null || poData.getBankCode().equals("")){
            ShowMessageFX.Warning(getStage(), "Invalid bank detected.", pxeModuleName, "Please verify your entry.");
            txtBankCode.requestFocus();
            return false;
        }
        
        if (poData.getCheckNox()== null || poData.getCheckNox().equals("")){
            ShowMessageFX.Warning(getStage(), "Invalid check number detected.", pxeModuleName, "Please verify your entry.");
            txtCheckNox.requestFocus();
            return false;
        }
        
        if (poData.getCheckDte() == null || poData.getCheckDte() == CommonUtils.toDate(pxeDateDefault)){
            ShowMessageFX.Warning(getStage(), "Invalid date detected.", pxeModuleName, "Please verify your entry.");
            txtCheckDte.requestFocus();
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
            case "txtbankcode":
                switch (event.getCode()){
                case F3:
                    XMBank loBank = new XMBank(poGRider, poGRider.getBranchCode(), true);
                    JSONObject loJSON = loBank.searchBank(lsValue, false);
                    
                    poData.setBankCode("");
                    if (loJSON != null){
                        poData.setBankCode((String) loJSON.get("sBankCode"));
                        txtField.setText((String) loJSON.get("sBankName"));
                        CommonUtils.SetNextFocus(txtField);
                    }
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
    
    private void loadData(){
        loadBank();
        txtCheckNox.setText(poData.getCheckNox());
        txtCheckDte.setText(poData.getCheckDte() == null ? "" : CommonUtils.xsDateMedium(poData.getCheckDte()));
        txtAmountxx.setText(CommonUtils.NumberFormat((Double) poData.getAmountxx(), "#,##0.00"));
        txtRemarksx.setText(poData.getRemarksx());
    }
    
    private void loadBank(){
        if (!poData.getBankCode().equals("")){
            XMBank loBank = new XMBank(poGRider, poGRider.getBranchCode(), true);
            JSONObject loJSON = loBank.searchBank(poData.getBankCode(), true);
            
            if (loJSON != null)
                txtBankCode.setText((String) loJSON.get("sBankName"));
            else
                txtBankCode.setText("");
        } else txtBankCode.setText("");
        
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

    public void setData(UnitCheckPaymentTrans foData){
        poData = foData;
    }
    
    public UnitCheckPaymentTrans getData(){
        return poData;
    }
    
    public boolean isCancelled(){return pbCancelled;}
    private UnitCheckPaymentTrans poData;
    private GRider poGRider;
    
    private final String pxeModuleName = "org.rmj.payment.agent.controller.CheckController";
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
        Date ldDate = null;
        
        if (lsValue == null) return;
            
        if(!nv){ /*Lost Focus*/
            switch (lsTxtNm.toLowerCase()){
                case "txtbankcode": break;
                case "txtchecknox": poData.setCheckNox(lsValue); break;
                case "txtcheckdte":
                    if (CommonUtils.isDate(lsValue, pxeDateFormat)){
                        poData.setCheckDte(CommonUtils.toDate(lsValue));
                    } else{
                        ShowMessageFX.Warning(getStage(), "Invalid reference date.", pxeModuleName, "Date format must be yyyy-MM-dd (e.g. 1991-07-07)");
                        poData.setCheckDte(null);
                        txtField.requestFocus();
                        return;
                    }
                    
                    txtField.setText(poData.getCheckDte() == null ? "" : CommonUtils.xsDateMedium(poData.getCheckDte()));
                    break;
                case "txtamountxx":
                    try {
                        lnValue = Double.parseDouble(lsValue);
                    } catch (Exception e) {
                        ShowMessageFX.Warning(getStage(), "Invalid amount format.", pxeModuleName, "Please verify your entry.");
                        txtField.requestFocus();
                        return;
                    }
                    
                    poData.setAmountxx(lnValue);
                    txtField.setText(CommonUtils.NumberFormat(lnValue, "###0.00"));
                    break;
                default:
                    ShowMessageFX.Information(getStage(), "Textfield " + lsTxtNm + " is not initialized.", pxeModuleName, "Please inform MIS department.");
            }
        } else
            if (lsTxtNm.toLowerCase().equals("txtcheckdte")){
                txtField.setText(poData.getCheckDte()== null ? "" : CommonUtils.xsDateShort(poData.getCheckDte()));
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
