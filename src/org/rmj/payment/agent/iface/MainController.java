/**
 * Payment Main Form Controller Class
 *
 * @author Michael Cuison
 * @since August 18, 2018
 */

package org.rmj.payment.agent.iface;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.MiscUtil;
import org.rmj.appdriver.SQLUtil;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.payment.pojo.UnitCheckPaymentTrans;
import org.rmj.payment.pojo.UnitCreditCardTrans;
import org.rmj.payment.pojo.UnitFinancerTrans;
import org.rmj.payment.pojo.UnitGCPaymentTrans;

public class MainController implements Initializable {
    @FXML
    private Label lblReceiptType;
    @FXML
    private Label lblTransNox;
    @FXML
    private TextField txtReferNox;
    @FXML
    private TextField txtClientNm;
    @FXML
    private TextField txtAddressx;
    @FXML
    private TextField txtTINumber;
    @FXML
    private TextField txtBusStyle;
    @FXML
    private TextField txtSCPWDIDx;
    @FXML
    private Label lblAmountPaid;
    @FXML
    private Label lblChange;
    @FXML
    private Label lblGiftCert;
    @FXML
    private Label lblOthers;
    @FXML
    private Label lblCheckAmount;
    @FXML
    private Label lblCreditCard;
    @FXML
    private TextField txtCashAmtx;
    @FXML
    private TextField txtOtherAmt;
    @FXML
    private Label lblSubTotal;
    @FXML
    private Label lblVATExclusive;
    @FXML
    private Label lblDiscount;
    @FXML
    private Label lblNetSales;
    @FXML
    private Label lbllAddVAT;
    @FXML
    private Label lblAmountDue;
    @FXML
    private Label lblVATAmount;
    @FXML
    private Label lblVATExempt;
    @FXML
    private Label lblZeroRated;
    @FXML
    private AnchorPane acMain;
    @FXML
    private Button cmdSave;
    @FXML
    private Button cmdCancel;
    @FXML
    private CheckBox chkSCPWD;
    @FXML
    private Button cmdCreditCard;
    @FXML
    private Button cmdCheck;
    @FXML
    private Button cmdGiftCert;
    @FXML
    private Button cmdOthers;
    @FXML
    private Label lblVATable;
    
    private static GRider poGrider;
    private static String psClientID;
    
    private double pnTranTotl = 0.00;
    private double pnVATExcls = 0.00;
    private double pnDiscount = 0.00;
    private double pnAddDiscx = 0.00;
    private double pnFreightx = 0.00;
    private double pnVATRatex = 0.00;
    
    private double pnTendered = 0.00;
    private double pnCashAmtx = 0.00;
    private double pnCredtCrd = 0.00;
    private double pnCheckPay = 0.00;
    private double pnGiftCert = 0.00;
    private double pnFinancer = 0.00;
    
    private double pnVATSales = 0.00;
    private double pnVATAmntx = 0.00;
    
    private double pnZroRtSle = 0.00;
    private double pnVatExSle = 0.00;
    private double pnCWTAmtxx = 0.00;
    
    private String psORNumber = "";
    private String psSourceCd = "";
    
    public void setGRider(GRider foGRider){
        poGrider = foGRider;
    }
    
    public void setClientID(String fsClientID){
        psClientID = fsClientID;
    }
    
    public void setVATRate(double fnVATRatex){
        pnVATRatex = fnVATRatex;
    }
    
    public void setTranTotl(double fnTranTotl){
        pnTranTotl = fnTranTotl;
        pnCashAmtx = fnTranTotl;
    }
    
    public void setFreight(double fnFreightx){
        pnFreightx = fnFreightx;
    }
    
    public void setVATExclusive(double fnVATExcls){
        pnVATExcls = fnVATExcls;
    }
    
    public void setDiscount(double fnDiscount){
        pnDiscount = fnDiscount;
    }
    
    public void setAddtnlDiscount(double fnAddDiscx){
        pnAddDiscx = fnAddDiscx;
    }
    
    public void setVATableSales(double fnVATSales){
        pnVATSales = fnVATSales;
    }
    
    public void setVATableAmntx(double fnVATAmntx){
        pnVATAmntx = fnVATAmntx;
    }
    
    public void setORNumber(String fsORNumber){
        psORNumber = fsORNumber;
    }
    
    public void setSourceCd(String fsSourceCd){
        psSourceCd = fsSourceCd;
    }
    
    public double getVATableSales(){
        return pnVATSales;
    }
    
    public double getVATableAmntx(){
        return pnVATAmntx;
    }
    
    public double getNonVATSl(){
        return pnVatExSle;
    }
    
    public double getZroVATSl(){
        return pnZroRtSle;
    }
    
    public double getCWTAmtxx(){
        return pnCWTAmtxx;
    }
    
    public boolean isCancelled(){
        return pbCancelled;
    }
    
    public String getORNumber(){
        return psORNumber;
    }
    
    public double getCashAmtx(){
        return pnCashAmtx;
    }
    
    public double getCredtCrd(){
        return pnCredtCrd;
    }
    
    public double getCheckPay(){
        return pnCheckPay;
    }
    
    public double getGiftCert(){
        return pnGiftCert;
    }
    
    public double getFinancer(){
        return pnFinancer;
    }
    
    public double getTenderedCash(){
        return pnTendered;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmdCreditCard.setOnAction(this::cmdButton_Click);
        cmdCheck.setOnAction(this::cmdButton_Click);
        cmdGiftCert.setOnAction(this::cmdButton_Click);
        cmdOthers.setOnAction(this::cmdButton_Click);  
        cmdCancel.setOnAction(this::cmdButton_Click);
        cmdSave.setOnAction(this::cmdButton_Click);
        chkSCPWD.setOnAction(this::chkButton_Click);
        
        txtReferNox.focusedProperty().addListener(txtField_Focus);
        txtClientNm.focusedProperty().addListener(txtField_Focus);
        txtAddressx.focusedProperty().addListener(txtField_Focus);
        txtTINumber.focusedProperty().addListener(txtField_Focus);
        txtBusStyle.focusedProperty().addListener(txtField_Focus);
        txtSCPWDIDx.focusedProperty().addListener(txtField_Focus);
        txtCashAmtx.focusedProperty().addListener(txtField_Focus);
        
        txtReferNox.setOnKeyPressed(this::txtField_KeyPressed);
        txtClientNm.setOnKeyPressed(this::txtField_KeyPressed);
        txtAddressx.setOnKeyPressed(this::txtField_KeyPressed);
        txtTINumber.setOnKeyPressed(this::txtField_KeyPressed);
        txtBusStyle.setOnKeyPressed(this::txtField_KeyPressed);
        txtSCPWDIDx.setOnKeyPressed(this::txtField_KeyPressed);
        txtCashAmtx.setOnKeyPressed(this::txtField_KeyPressed);
        chkSCPWD.setOnKeyPressed(this::Check_KeyPressed);
        
        clearFields();
        loadClient();
        
        txtReferNox.setText(psORNumber);
        switch (psSourceCd){
            case "SL":
                lblReceiptType.setText("Sales Invoice");
                break;
            case "JO":
                lblReceiptType.setText("Official Receipt");
                break;
            case "SO":
                lblReceiptType.setText("Provisionary Receipt");
                break;
            default:
                
        }
        
        pnCashAmtx = (pnVATSales + pnVATAmntx) - (pnCredtCrd + pnCheckPay + pnGiftCert + pnFinancer);
        
        computeTax();
        computePay();
        
        pbLoaded = true;
        
        txtCashAmtx.requestFocus();
    }

    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
        
        switch (lsButton.toLowerCase()){
            case "cmdsave":
                Double lnValue = 0.00;
                String lsValue = txtCashAmtx.getText();
                
                try {
//                    lnValue = Double.parseDouble(String.valueOf(lsValue));
                    lnValue = Double.parseDouble(lsValue.replace(",", ""));
                } catch (Exception e) {
                    txtCashAmtx.setText(CommonUtils.NumberFormat(pnCashAmtx, "#,##0.00"));
                    return;
                }
                    
                pnTendered = lnValue;
                pnCashAmtx = lnValue;
                txtCashAmtx.setText(CommonUtils.NumberFormat(pnCashAmtx, "#,##0.00"));

                if ((pnCashAmtx + pnCredtCrd + pnCheckPay + pnGiftCert + pnFinancer) < Math.round(pnVATSales + pnVATAmntx)){
                    ShowMessageFX.Warning("Payment is not enough for the transaction cost.", pxeModuleName, "Please verify your entry.");
                    txtCashAmtx.requestFocus();
                    return;
                } else{
                    if (Math.round(pnCredtCrd)  > Math.round(pnVATSales + pnVATAmntx)){
                        ShowMessageFX.Warning("It seems that credit card amount is greater than the transaction total.", pxeModuleName, "Please verify your entry.");
                        return;
                    }
                }
                
                if (psORNumber.equals("")){
                    ShowMessageFX.Warning("OR/SI number not detected.", pxeModuleName, "Please verify your entry.");
                    txtReferNox.requestFocus(); 
                    return;
                } 
                
                //compute how much will be deducted from cash tendered base on the other payments issued.
                double lnAmtPaid = pnCredtCrd + pnCheckPay + pnGiftCert + pnFinancer;
                lnAmtPaid = Math.round(pnVATSales + pnVATAmntx) - lnAmtPaid;
                pnCashAmtx = lnAmtPaid;
                
                pbCancelled = false;
                unloadScene(event);
                break;
            case "cmdcancel":
                pbCancelled = true;
                unloadScene(event);
                break;
            case "cmdcreditcard":
                showCreditCardPay();
                break;
            case "cmdcheck": 
                showCheckPayment();
                break;
            case "cmdgiftcert":
                showGiftCertPayment();
                break;
            case "cmdothers":
                showFinancingPayment();
                break;
            default:
                ShowMessageFX.Information(getStage(), "Button " + lsButton + " is not initialized.", pxeModuleName, "Please inform MIS department.");
        }
    }
    
    private void chkButton_Click(ActionEvent event){
        CheckBox loChk = (CheckBox)event.getSource();
        
        if (loChk.isSelected()){
            txtSCPWDIDx.setDisable(false);
        } else{
            txtSCPWDIDx.setText("");
            txtSCPWDIDx.setDisable(true);            
        }    
    }

    private void clearFields(){
        /*top pane*/
        lblTransNox.setText("");
        txtReferNox.setText("");
        txtClientNm.setText("");
        txtAddressx.setText("");
        txtTINumber.setText("");
        txtBusStyle.setText("");
        txtSCPWDIDx.setText("");
        chkSCPWD.setSelected(false);
        txtSCPWDIDx.setDisable(true);
        
        /*left of center pane*/
        txtCashAmtx.setText("0.00");
        lblCreditCard.setText("0.00");
        lblCheckAmount.setText("0.00");
        lblGiftCert.setText("0.00");
        lblOthers.setText("0.00");
        lblAmountPaid.setText("0.00");
        lblChange.setText("0.00");
        
        /*right of center pane*/
        lblSubTotal.setText("0.00");
        lblVATExclusive.setText("0.00");
        lblDiscount.setText("0.00");
        lblNetSales.setText("0.00");
        lbllAddVAT.setText("0.00");
        lblAmountDue.setText("0.00");
        
        lblVATable.setText("0.00");
        lblVATAmount.setText("0.00");
        lblVATExempt.setText("0.00");
        lblZeroRated.setText("0.00");
    }
    
    private void computeTax(){
        double lnTranTotl = pnTranTotl + pnFreightx;
        double lnVATExlcv = lnTranTotl / pnVATRatex;
        double lnDiscount = ((lnTranTotl * pnDiscount) / pnVATRatex) + (pnAddDiscx / pnVATRatex);
        double lnNetSales = lnVATExlcv - lnDiscount;
        double lnAddVATxx = lnNetSales * (pnVATRatex - 1);
        double lnAmoutDue = lnNetSales + lnAddVATxx;
                
        lblSubTotal.setText(CommonUtils.NumberFormat(lnTranTotl, "#,##0.00"));
        lblVATExclusive.setText(CommonUtils.NumberFormat(lnVATExlcv, "#,##0.00")); //lnVATExlcv
        lblDiscount.setText(CommonUtils.NumberFormat(lnDiscount, "#,##0.00")); //lnDiscount
        lblNetSales.setText(CommonUtils.NumberFormat(lnNetSales, "#,##0.00")); //lnNetSales
        lbllAddVAT.setText(CommonUtils.NumberFormat(lnAddVATxx, "#,##0.00")); //lnAddVATxx
        lblAmountDue.setText(CommonUtils.NumberFormat(lnAmoutDue, "#,##0.00")); //lnAmoutDue
        
        lblVATable.setText(CommonUtils.NumberFormat(lnNetSales, "#,##0.00"));
        lblVATAmount.setText(CommonUtils.NumberFormat(lnAddVATxx, "#,##0.00"));
        lblVATExempt.setText(CommonUtils.NumberFormat(0.00, "#,##0.00"));
        lblZeroRated.setText(CommonUtils.NumberFormat(0.00, "#,##0.00"));
        
        pnVATSales = lnNetSales;
        pnVATAmntx = lnAddVATxx;
    
        pnZroRtSle = 0.00;
        pnVatExSle = 0.00;
        pnCWTAmtxx = 0.00;
    }
    
    private void computePay(){        
        //pnCashAmtx = pnCashAmtx - (pnCredtCrd + pnCheckPay + pnGiftCert + pnAdvPaymx);
        
        //txtCashAmtx.setText(CommonUtils.NumberFormat(pnCashAmtx, "###0.00"));
//        txtOtherAmt.setText(CommonUtils.NumberFormat(pnCredtCrd + pnCheckPay + pnGiftCert + pnFinancer, "#,##0.00"));
        lblCreditCard.setText(CommonUtils.NumberFormat(pnCredtCrd, "#,##0.00"));
        lblCheckAmount.setText(CommonUtils.NumberFormat(pnCheckPay, "#,##0.00"));
        lblGiftCert.setText(CommonUtils.NumberFormat(pnGiftCert, "#,##0.00"));
        lblOthers.setText(CommonUtils.NumberFormat(pnFinancer, "#,##0.00"));
        
        lblAmountPaid.setText(CommonUtils.NumberFormat(pnCashAmtx +  pnCredtCrd + pnCheckPay + pnGiftCert + pnFinancer, "#,##0.00"));
        lblChange.setText(CommonUtils.NumberFormat((pnCashAmtx +  pnCredtCrd + pnCheckPay + pnGiftCert + pnFinancer) - (pnVATSales + pnVATAmntx), "#,##0.00"));
    }
    
    private void showCreditCardPay(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("creditcard.fxml"));
        
        CreditcardController loCreditCard = new CreditcardController();
        loCreditCard.setGRider(poGrider);
        loCreditCard.setData(poCreditCard);
        
        fxmlLoader.setController(loCreditCard);
        Parent parent;
        
        try {
            parent = fxmlLoader.load();
            
            Stage stage = new Stage();
        
            parent.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });

            Scene scene = new Scene(parent);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.showAndWait();
            
            if (!loCreditCard.isCancelled()){
                poCreditCard = loCreditCard.getData();
                pnCredtCrd = (Double) poCreditCard.getAmountxx();
            }
            
            computePay();
        } catch (IOException ex) {
            ShowMessageFX.Error(getStage(), ex.getMessage(), pxeModuleName, "Please inform MIS department.");
            System.exit(1);
        }
    }
    
    private void showCheckPayment(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("check.fxml"));
        
        CheckController loCheck = new CheckController();
        loCheck.setGRider(poGrider);
        loCheck.setData(poCheck);
        
        fxmlLoader.setController(loCheck);
        Parent parent;
        
        try {
            parent = fxmlLoader.load();
            
            Stage stage = new Stage();
        
            parent.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });

            Scene scene = new Scene(parent);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.showAndWait();
            
            if (!loCheck.isCancelled()){
                poCheck = loCheck.getData();
                pnCheckPay = (Double) poCheck.getAmountxx();
            }
            
            computePay();
        } catch (IOException ex) {
            ShowMessageFX.Error(getStage(), ex.getMessage(), pxeModuleName, "Please inform MIS department.");
            System.exit(1);
        }
    }
    
    private void showGiftCertPayment(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("giftcheck.fxml"));
        
        GiftcheckController loGiftCheck = new GiftcheckController();
        loGiftCheck.setGRider(poGrider);
        loGiftCheck.setData(poGiftCert);
        
        fxmlLoader.setController(loGiftCheck);
        Parent parent;
        
        try {
            parent = fxmlLoader.load();
            
            Stage stage = new Stage();
        
            parent.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });

            Scene scene = new Scene(parent);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.showAndWait();
            
            if (!loGiftCheck.isCancelled()){
                poGiftCert = loGiftCheck.getData();
                pnGiftCert = (Double) poGiftCert.getAmountxx();
            }
            
            computePay();
        } catch (IOException ex) {
            ShowMessageFX.Error(getStage(), ex.getMessage(), pxeModuleName, "Please inform MIS department.");
            System.exit(1);
        }
    }
    
    private void showFinancingPayment(){
        double lnDiscount = ((pnTranTotl * pnDiscount) / pnVATRatex) + (pnAddDiscx);
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("financer.fxml"));
        
        FinancerController loFinancer = new FinancerController();
        loFinancer.setGRider(poGrider);
        poFinancer.setFinAmtxx((pnTranTotl - (lnDiscount))  - (pnCashAmtx + pnCheckPay + pnGiftCert));
        loFinancer.setData(poFinancer);
        
       
        fxmlLoader.setController(loFinancer);
        Parent parent;
        
        try {
            parent = fxmlLoader.load();
            
            Stage stage = new Stage();
        
            parent.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });

            Scene scene = new Scene(parent);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.showAndWait();
            
            if (!loFinancer.isCancelled()){
                poFinancer = loFinancer.getData();
                pnFinancer = (Double) poFinancer.getAmtPaidx();
            }
            
            computePay();
        } catch (IOException ex) {
            ShowMessageFX.Error(getStage(), ex.getMessage(), pxeModuleName, "Please inform MIS department.");
            System.exit(1);
        }
    }
        
    private void loadClient(){
        String lsSQL = "SELECT" +
                            "  a.sClientID" + 
                            ", a.sClientNm" + 
                            ", CONCAT(b.sHouseNox, ' ', b.sAddressx, ' ', c.sTownName, ' ', c.sZippCode, ' ', d.sProvName) xAddressx" + 
                        " FROM Client_Master a" +
                            " LEFT JOIN Client_Address b" + 
                                " ON a.sClientID = b.sClientID" +
                            " LEFT JOIN TownCity c" + 
                                " ON b.sTownIDxx = c.sTownIDxx" +
                            " LEFT JOIN Province d" + 
                                " ON c.sProvIDxx = d.sProvIDxx" + 
                        " WHERE a.sClientID = " + SQLUtil.toSQL(psClientID);
        
        ResultSet loRS = poGrider.executeQuery(lsSQL);
        if (MiscUtil.RecordCount(loRS) == 0){
            ShowMessageFX.Warning("No client id detected.", pxeModuleName, "Please verify your entry.");
            System.exit(0);
        }
        
        try {
            loRS.first();
            txtClientNm.setText(loRS.getString("sClientNm"));
            txtAddressx.setText(loRS.getString("xAddressx"));
        } catch (SQLException e) {
            ShowMessageFX.Error(e.getMessage(), pxeModuleName, "Please inform MIS Department.");
            System.exit(1);
        }
    }
    
    private void txtField_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField)event.getSource();
             
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
    
    private void Check_KeyPressed(KeyEvent event){
        CheckBox chkField = (CheckBox)event.getSource();
        
        if (null != event.getCode()){
            switch (event.getCode()) {
                case ENTER:
                case DOWN:
                    CommonUtils.SetNextFocus(chkField);
                    break;
                case UP:
                    CommonUtils.SetPreviousFocus(chkField);
                    break;
                default:
                    break;
            }
        }
    }
    
    private void unloadScene(ActionEvent event){
        Node source = (Node)  event.getSource(); 
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
    
    private Stage getStage(){
        return (Stage) lblTransNox.getScene().getWindow();
    }
    
    @Override
    public String toString() {
        return pxeModuleName;
    }
    
    public UnitCreditCardTrans getUnitCreditCard(){
        return poCreditCard;
    }
    
    public void setUnitCreditCard(UnitCreditCardTrans foData){
        poCreditCard = foData;
    }
    
    public UnitCheckPaymentTrans getUnitCheckPayment(){
        return poCheck;
    }
    
    public void setUnitCheckPayment(UnitCheckPaymentTrans foData){
        poCheck = foData;
    }
    
    public UnitGCPaymentTrans getUnitGiftCert(){
        return poGiftCert;
    }
    
    public void setUnitGiftCert (UnitGCPaymentTrans foData){
        poGiftCert = foData;
    }
    
    public UnitFinancerTrans getUnitFinancer(){
        return poFinancer;
    }
    
    public void setUnitFinancer (UnitFinancerTrans foData){
        poFinancer = foData;
    }
    
    private double xOffset = 0; 
    private double yOffset = 0;
    
    private UnitCreditCardTrans poCreditCard;
    private UnitCheckPaymentTrans poCheck;
    private UnitGCPaymentTrans poGiftCert;
    private UnitFinancerTrans poFinancer;
    
    private boolean pbLoaded = false;
    private boolean pbCancelled = true;
    private final String pxeModuleName = this.getClass().getName();
    
    final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{
        if (!pbLoaded) return;
        
        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        String lsTxtNm = txtField.getId();
        String lsValue = txtField.getText();
        
        int lnVal = 0;
        double lnValue = 0.0;
        
        if (lsValue == null) return;
            
        if(!nv){ /*Lost Focus*/
            switch (lsTxtNm.toLowerCase()){
                case "txtrefernox": break;
                case "txtclientnm": break;
                case "txtaddressx": break;
                case "txttinumber": break;
                case "txtbusstyle": break;
                case "txtscpwdidx": break;
                case "txtcashamtx": 
                    try {
                        lnValue = Double.parseDouble(lsValue);
                    } catch (Exception e) {
                        txtField.setText(CommonUtils.NumberFormat(pnCashAmtx, "###0.00"));
                        return;
                    }
                    
                    pnTendered = lnValue;
                    pnCashAmtx = lnValue;
                    txtCashAmtx.setText(CommonUtils.NumberFormat(pnCashAmtx, "#,##0.00"));
                    
                    computePay();
                    break;
                case "txtotheramt": break;
                default:
                    ShowMessageFX.Information(getStage(), "Textfield " + lsTxtNm + " is not initialized.", pxeModuleName, "Please inform MIS department.");
            }
        } else
            txtField.selectAll();
    };
}
