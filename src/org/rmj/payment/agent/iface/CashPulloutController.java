package org.rmj.payment.agent.iface;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.rmj.appdriver.StringUtil;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;

public class CashPulloutController implements Initializable {
    @FXML
    private Button btnOkay;
    @FXML
    private Button btnCancel;
    @FXML
    private TextField txtField01;
    @FXML
    private TextField txtField02;
    @FXML
    private TextField txtField03;
    @FXML
    private TextField txtField04;
    @FXML
    private TextField txtField05;
    @FXML
    private TextField txtField07;
    @FXML
    private TextField txtField08;
    @FXML
    private TextField txtField06;
    @FXML
    private TextField txtField09;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pbCancel = true;
        
        txtField01.setText(CommonUtils.NumberFormat(nOpenAmtx, "#,##0.00"));
        txtField02.setText(CommonUtils.NumberFormat(nCashAmtx, "#,##0.00"));
        txtField03.setText(CommonUtils.NumberFormat(nCheckAmt, "#,##0.00"));
        txtField04.setText(CommonUtils.NumberFormat(nCredtCrd, "#,##0.00"));
        txtField05.setText(CommonUtils.NumberFormat(nGiftCert, "#,##0.00"));
        txtField06.setText(CommonUtils.NumberFormat(nChargexx, "#,##0.00"));
        txtField07.setText(CommonUtils.NumberFormat(nWithdraw, "#,##0.00"));
        txtField08.setText(CommonUtils.NumberFormat(nDepositx, "#,##0.00"));
        txtField09.setText(CommonUtils.NumberFormat(nFinAmntx, "#,##0.00"));
    }    

    @FXML
    private void btnOkay_Click(ActionEvent event) {
        if (ShowMessageFX.YesNo(getStage(event), "Do you want to withdraw cash?", "Confirm", "Cash Pullout")){
            String lsValue = ShowMessageFX.InputText(getStage(event), null, "Cash Pullout", "Please enter cash amount to withdraw.");
        
            if (StringUtil.isNumeric(lsValue))
                nCPullOut = Double.parseDouble(lsValue);
            else
                nCPullOut = 0.00;
        }
        
        pbCancel = false;
        unloadScene(event);
    }

    @FXML
    private void btnCancel_Click(ActionEvent event) {
        pbCancel = true;
        unloadScene(event);
    }
        
    private void unloadScene(ActionEvent event){
        Node source = (Node)  event.getSource(); 
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
    
    private Stage getStage(ActionEvent event){
        Node source = (Node)  event.getSource(); 
        return (Stage) source.getScene().getWindow();
    }
    
    public void setDate(String fsValue){
        psDate = fsValue;
    }
    
    public void setMachineNo(String fsValue){
        psMachineNo = fsValue;
    }
    
    public void setCashier(String fsValue){
        psCashier = fsValue;
    }
    
    public void setOpeningBalance(Number fnValue){nOpenAmtx = fnValue;}
    public void setCashAmount(Number fnValue){nCashAmtx = fnValue;}
    public void setCheckAmount(Number fnValue){nCheckAmt = fnValue;}
    public void setChargeAmount(Number fnValue){nChargexx = fnValue;}
    public void setCreditCardAmount(Number fnValue){nCredtCrd = fnValue;}
    public void setGiftCertAmount(Number fnValue){nGiftCert = fnValue;}
    public void setFinanceAmount(Number fnValue){nFinAmntx = fnValue;}
    public void setWithrawal(Number fnValue){nWithdraw = fnValue;}
    public void setDeposit(Number fnValue){nDepositx = fnValue;}
    
    public Number getCashPullOut(){return nCPullOut;}
    public boolean isCancelled(){return pbCancel;}
    
    private boolean pbCancel;
    private String psDate;
    private String psMachineNo;
    private String psCashier;
    
    private Number nOpenAmtx = 0.00;
    private Number nCashAmtx = 0.00;
    private Number nCheckAmt = 0.00;
    private Number nChargexx = 0.00;
    private Number nCredtCrd = 0.00;
    private Number nGiftCert = 0.00;
    private Number nFinAmntx = 0.00;
    private Number nWithdraw = 0.00;
    private Number nDepositx = 0.00;
    private Number nCPullOut = 0.00;
}
