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
import org.rmj.cas.parameter.agent.XMBank;
import org.rmj.cas.parameter.agent.XMTerm;
import org.rmj.payment.agent.XMCreditCardTransMulti;

public class CreditcardControllerMulti implements Initializable {

    @FXML
    private AnchorPane acMain;
    @FXML
    private TextField txtBankCode;
    @FXML
    private TextField txtApprovNo;
    @FXML
    private TextField txtAmountxx;
    @FXML
    private TextField txtTermCode;
    @FXML
    private TableView table;
    @FXML
    private Button cmdCancel;
    @FXML
    private Button cmdOkay;
    @FXML
    private Button cmdAdd;
    @FXML
    private TextField txtTermnlID;
    @FXML
    private TextField txtCardNoxx;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        poTrans = new XMCreditCardTransMulti(poGRider, poGRider.getBranchCode(), false);
        
        cmdOkay.setOnAction(this::cmdButton_Click);
        cmdCancel.setOnAction(this::cmdButton_Click);
        cmdAdd.setOnAction(this::cmdButton_Click);
        
        txtTermnlID.focusedProperty().addListener(txtField_Focus);
        txtBankCode.focusedProperty().addListener(txtField_Focus);
        txtCardNoxx.focusedProperty().addListener(txtField_Focus);
        txtApprovNo.focusedProperty().addListener(txtField_Focus);
        txtAmountxx.focusedProperty().addListener(txtField_Focus);
        txtTermCode.focusedProperty().addListener(txtField_Focus);        
        
        txtTermnlID.setOnKeyPressed(this::txtField_KeyPressed);
        txtBankCode.setOnKeyPressed(this::txtField_KeyPressed);
        txtCardNoxx.setOnKeyPressed(this::txtField_KeyPressed);
        txtApprovNo.setOnKeyPressed(this::txtField_KeyPressed);
        txtAmountxx.setOnKeyPressed(this::txtField_KeyPressed);
        txtTermCode.setOnKeyPressed(this::txtField_KeyPressed);
                
//        loadData();
        
        pbLoaded = true;
    }
    
    private boolean isEntryOK(){
//        if (poData.getTermnlID()== null || poData.getTermnlID().equals("")){
//            ShowMessageFX.Warning(getStage(), "Invalid terminal detected.", pxeModuleName, "Please verify your entry.");
//            txtTermnlID.requestFocus();
//            return false;
//        }
//        
//        if (poData.getBankCode() == null || poData.getBankCode().equals("")){
//            ShowMessageFX.Warning(getStage(), "Invalid bank detected.", pxeModuleName, "Please verify your entry.");
//            txtBankCode.requestFocus();
//            return false;
//        }
        
//        if (poData.getCardNoxx()== null || poData.getCardNoxx().equals("")){
//            ShowMessageFX.Warning(getStage(), "Invalid card number detected.", pxeModuleName, "Please verify your entry.");
//            txtCardNoxx.requestFocus();
//            return false;
//        }
//        
//        if (poData.getApprovNo()== null || poData.getApprovNo().equals("")){
//            ShowMessageFX.Warning(getStage(), "Invalid approval number detected.", pxeModuleName, "Please verify your entry.");
//            txtApprovNo.requestFocus();
//            return false;
//        }
        
        //if (poData.getTermCode()== null || poData.getTermCode().equals("")){
        //    ShowMessageFX.Warning(getStage(), "Invalid term detected.", pxeModuleName, "Please verify your entry.");
        //    txtTermCode.requestFocus();
        //    return false;
        //}

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
                poTrans.addDetail();
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
            case "txttermnlid":
                switch (event.getCode()){
                case F3:
                    XMBank loBank = new XMBank(poGRider, poGRider.getBranchCode(), true);
                    JSONObject loJSON = loBank.searchBank(lsValue, false);
                    
//                    poData.setTermnlID("");
//                    if (loJSON != null){
//                        poData.setTermnlID((String) loJSON.get("sBankCode"));
//                        txtField.setText((String) loJSON.get("sBankName"));
//                        CommonUtils.SetNextFocus(txtField);
//                    }
                }
                break;
            case "txtbankcode":
                switch (event.getCode()){
                case F3:
                    XMBank loBank = new XMBank(poGRider, poGRider.getBranchCode(), true);
                    JSONObject loJSON = loBank.searchBank(lsValue, false);
                    
//                    poData.setBankCode("");
//                    if (loJSON != null){
//                        poData.setBankCode((String) loJSON.get("sBankCode"));
//                        txtField.setText((String) loJSON.get("sBankName"));
//                        CommonUtils.SetNextFocus(txtField);
//                    }
                }
                break;
            case "txttermcode":
                switch (event.getCode()){
                case F3:
                    XMTerm loTerm = new XMTerm(poGRider, poGRider.getBranchCode(), true);
                    JSONObject loJSON = loTerm.searchTerm(lsValue, false);
                    
//                    poData.setTermCode("");
                    if (loJSON != null){
//                        poData.setTermCode((String) loJSON.get("sTermCode"));
                        txtField.setText((String) loJSON.get("sDescript"));
                        CommonUtils.SetNextFocus(txtField);
                    }
                }
                break;
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
            }
        }
    }
    
//    private void loadData(){
//        loadTerminal();
//        loadBank();
//        loadTerm();
//        txtCardNoxx.setText(poData.getCardNoxx());
//        txtApprovNo.setText(poData.getApprovNo());
//        txtAmountxx.setText(CommonUtils.NumberFormat((Double) poData.getAmountxx(), "###0.00"));
//    }
    
//    private void loadTerminal(){        
//        if (!poData.getTermnlID().equals("")){
//            XMBank loBank = new XMBank(poGRider, poGRider.getBranchCode(), true);
//            JSONObject loJSON = loBank.searchBank(poData.getTermnlID(), true);
//            
//            if (loJSON != null)
//                txtTermnlID.setText((String) loJSON.get("sBankName"));
//            else
//                txtTermnlID.setText("");
//        } else txtTermnlID.setText("");
//    }
    
//    private void loadBank(){
//        if (!poData.getBankCode().equals("")){
//            XMBank loBank = new XMBank(poGRider, poGRider.getBranchCode(), true);
//            JSONObject loJSON = loBank.searchBank(poData.getBankCode(), true);
//            
//            if (loJSON != null)
//                txtBankCode.setText((String) loJSON.get("sBankName"));
//            else
//                txtBankCode.setText("");
//        } else txtBankCode.setText("");
//        
//    }
    
    private void loadTerm(){
//        if (!poData.getTermCode().equals("")){
//            XMTerm loTerm = new XMTerm(poGRider, poGRider.getBranchCode(), true);
//            JSONObject loJSON = loTerm.searchTerm(poTrans.getTermCode(), true);
//                    
//            if (loJSON != null)
//                txtTermCode.setText((String) loJSON.get("sDescript"));
//            else 
//                txtTermCode.setText("");
//        } else txtTermCode.setText("");
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

//    public void setData(UnitCreditCardTrans foData){
//        poData = foData;
//    }
//    
//    public UnitCreditCardTrans getData(){
//        return poData;
//    }
    
    public boolean isCancelled(){return pbCancelled;}
    private GRider poGRider;
    private XMCreditCardTransMulti poTrans;
    
    private final String pxeModuleName = "org.rmj.payment.agent.iface.CreditcardController";
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
                case "txttermnlid":
                case "txtbankcode": break;
                case "txtcardnoxx": 
//                    poData.setCardNoxx(lsValue); break;
                case "txtapprovno": 
//                    poData.setApprovNo(lsValue); break;
                case "txtamountxx": 
                    try {
                        lnValue = Double.parseDouble(lsValue);
                    } catch (Exception e) {
                        ShowMessageFX.Warning(getStage(), "Invalid amount format.", pxeModuleName, "Please verify your entry.");
                        txtField.requestFocus();
                        return;
                    }
                    
//                    poData.setAmountxx(lnValue);
                    txtField.setText(CommonUtils.NumberFormat(lnValue, "###0.00"));
                    break;
                case "txttermcode": break;
                default:
                    ShowMessageFX.Information(getStage(), "Textfield " + lsTxtNm + " is not initialized.", pxeModuleName, "Please inform MIS department.");
            }
        } else
            txtField.selectAll();
    };
}