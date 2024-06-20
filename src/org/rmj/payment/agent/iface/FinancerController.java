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
import org.rmj.cas.parameter.agent.XMCP_Financer;
import org.rmj.cas.parameter.agent.XMTerm;
import org.rmj.payment.pojo.UnitFinancerTrans;

public class FinancerController implements Initializable {
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
    private TextField txtReferNox;
    @FXML
    private TextField txtFinAmtxx;
    @FXML
    private TextField txtAmtPaidx;
    @FXML
    private TextField txtTerm;
    @FXML
    private TextArea txtRemarksx;
    @FXML
    private TableView table;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (poData == null){
            ShowMessageFX.Warning("Financer data is not set.", pxeModuleName, "Please inform MIS Department.");
            System.exit(0);
        }
        
        cmdOkay.setOnAction(this::cmdButton_Click);
        cmdCancel.setOnAction(this::cmdButton_Click);
        cmdAdd.setOnAction(this::cmdButton_Click);
       
        txtCompnyCd.focusedProperty().addListener(txtField_Focus);
        txtReferNox.focusedProperty().addListener(txtField_Focus);
        txtFinAmtxx.focusedProperty().addListener(txtField_Focus);
        txtAmtPaidx.focusedProperty().addListener(txtField_Focus);
        txtTerm.focusedProperty().addListener(txtField_Focus);
        txtRemarksx.focusedProperty().addListener(txtArea_Focus);
        
        txtCompnyCd.setOnKeyPressed(this::txtField_KeyPressed);
        txtReferNox.setOnKeyPressed(this::txtField_KeyPressed);
        txtFinAmtxx.setOnKeyPressed(this::txtField_KeyPressed);
        txtAmtPaidx.setOnKeyPressed(this::txtField_KeyPressed);
        txtTerm.setOnKeyPressed(this::txtField_KeyPressed);
        txtRemarksx.setOnKeyPressed(this::txtFieldArea_KeyPressed);
        
        loadData();
        
        pbLoaded = true;
    }
    
    private boolean isEntryOK(){
        if (poData.getFinancer()== null || poData.getFinancer().equals("")){
            ShowMessageFX.Warning(getStage(), "Invalid affiliated company detected.", pxeModuleName, "Please verify your entry.");
            txtCompnyCd.requestFocus();
            return false;
        }
        
        if (poData.getReferNox()== null || poData.getReferNox().equals("")){
            ShowMessageFX.Warning(getStage(), "Invalid control number detected.", pxeModuleName, "Please verify your entry.");
            txtReferNox.requestFocus();
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
                        XMCP_Financer loCompany = new XMCP_Financer(poGRider, poGRider.getBranchCode(), true);
                        JSONObject loJSON = loCompany.searchCPFinancer(lsValue, false);
                        
                        if (loJSON != null){
                            poData.setFinancer((String) loJSON.get("sFnancrID"));
                            txtField.setText((String) loJSON.get("sCompnyNm"));
                            CommonUtils.SetNextFocus(txtField);
                        } else poData.setFinancer("");
                }
                break;
            case "txtterm":
                switch (event.getCode()){
                    case F3:
                        XMTerm loTerm = new XMTerm(poGRider, poGRider.getBranchCode(), true);
                        JSONObject loJSON = loTerm.searchTerm(lsValue, false);
                        
                        if (loJSON != null){
                            poData.setTermIDxx((String) loJSON.get("sTermCode"));
                            txtField.setText((String) loJSON.get("sDescript"));
                            CommonUtils.SetNextFocus(txtField);
                        } else poData.setTermIDxx("");
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
                default:
                    break;
            }
        }
    }
    
    private void loadData(){
        loadCompany();
        loadTerm();
        txtReferNox.setText(poData.getReferNox());
        txtFinAmtxx.setText(CommonUtils.NumberFormat((Double) poData.getFinAmtxx(), "#,##0.00"));
        txtAmtPaidx.setText(CommonUtils.NumberFormat((Double) poData.getAmtPaidx(), "#,##0.00"));
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
        if (!poData.getFinancer().equals("")){
            XMCP_Financer loCompany = new XMCP_Financer(poGRider, poGRider.getBranchCode(), true);
            JSONObject loJSON = loCompany.searchCPFinancer(poData.getFinancer(), true);

            if (loJSON != null){
                poData.setFinancer((String) loJSON.get("sFnancrID"));
                txtCompnyCd.setText((String) loJSON.get("sCompnyNm"));
            }  else txtCompnyCd.setText("");
        } else txtCompnyCd.setText("");
        
    }
    
    private void loadTerm(){
        if (!poData.getTermIDxx().equals("")){
            XMTerm loTerm = new XMTerm(poGRider, poGRider.getBranchCode(), true);
            JSONObject loJSON = loTerm.searchTerm(poData.getTermIDxx(), true);

            if (loJSON != null){
                poData.setTermIDxx((String) loJSON.get("sTermCode"));
                txtTerm.setText((String) loJSON.get("sDescript"));
            }  else txtTerm.setText("");
        } else txtTerm.setText("");
        
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

    public void setData(UnitFinancerTrans foData){
        poData = foData;
    }
    
    public UnitFinancerTrans getData(){
        return poData;
    }
    
    public boolean isCancelled(){return pbCancelled;}
    private UnitFinancerTrans poData;
    private GRider poGRider;
    
    private final String pxeModuleName = "org.rmj.payment.agent.controller.FinancerController";
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
                case "txtamtpaidx":
                    try {
                        lnValue = Double.parseDouble(lsValue);
                    } catch (Exception e) {
                        ShowMessageFX.Warning(getStage(), "Invalid amount format.", pxeModuleName, "Please verify your entry.");
                        return;
                    }
                    
                    poData.setAmtPaidx(lnValue);
                    txtField.setText(CommonUtils.NumberFormat(lnValue, "###0.00"));
                    break;
                case "txtterm": break;
                default:
                    ShowMessageFX.Information(getStage(), "Textfield " + lsTxtNm + " is not initialized.", pxeModuleName, "Please inform MIS department.");
            }
        } else
            txtField.selectAll();
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