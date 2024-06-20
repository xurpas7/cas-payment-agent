/**
 * @author  Michael Cuison
 * 
 * @since    2018-09-06
 */
package org.rmj.payment.agent;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.MiscUtil;
import org.rmj.appdriver.SQLUtil;
import org.rmj.payment.agent.constant.PaymentType;
import org.rmj.payment.base.ORMaster;
import org.rmj.payment.agent.iface.MainController;
import org.rmj.payment.pojo.UnitCheckPaymentTrans;
import org.rmj.payment.pojo.UnitCreditCardTrans;
import org.rmj.payment.pojo.UnitGCPaymentTrans;
import org.rmj.payment.pojo.UnitReceiptMaster;
import org.rmj.payment.pojo.UnitFinancerTrans;

public class XMORMaster{
    public XMORMaster(GRider foGRider, String fsBranchCd, boolean fbWithParent){
        this.poGRider = foGRider;
        if(foGRider != null){
            pbWithParnt = fbWithParent;
            psBranchCd = fsBranchCd;
         
            poCtrl = new ORMaster();
            poCtrl.setGRider(foGRider);
            poCtrl.setBranch(psBranchCd);
            poCtrl.setWithParent(true);
         
            pnEditMode = EditMode.UNKNOWN;

            poData = new UnitReceiptMaster();
        }
    }

    public double getAmountPaid(){
        return pnAmtPaidx;
    }
    
    public String getMessage(){
        return psMessagex;
    }    
    
    public void setMaster(int fnCol, Object foData) {
        if (pnEditMode != EditMode.UNKNOWN){
            // Don't allow specific fields to assign values
            if(!(fnCol == poData.getColumn("sTransNox") ||
                fnCol == poData.getColumn("dModified"))){
                
                poData.setValue(fnCol, foData);
            }
        }
    }

    public void setMaster(String fsCol, Object foData) {
        setMaster(poData.getColumn(fsCol), foData);
    }

    public Object getMaster(int fnCol) {
        if(pnEditMode == EditMode.UNKNOWN || poCtrl == null)
            return null;
        else return poData.getValue(fnCol);
    }

    public Object getMaster(String fsCol) {
        return getMaster(poData.getColumn(fsCol));
    }

    public boolean newTransaction() {
        poData = poCtrl.newTransaction();
        
        //initiallize other payment
        poCreditCard = new UnitCreditCardTrans();
        poCheck = new UnitCheckPaymentTrans();
        poGiftCert = new UnitGCPaymentTrans();
        poFinancer = new UnitFinancerTrans();
        
        if (poData == null){
            psMessagex = poCtrl.getErrMsg() + "\n" +  poCtrl.getMessage();
            return false;
        }else{
            pnEditMode = EditMode.ADDNEW;
            return true;
        }
    }

    public boolean loadTransaction(String fsSourceNo, String fsSourceCd){
        String lsSQL = MiscUtil.addCondition(MiscUtil.makeSelect(new UnitReceiptMaster()),
                                                "sTransNox LIKE " + SQLUtil.toSQL(psBranchCd + System.getProperty("pos.clt.trmnl.no") + "%"));
        
        lsSQL = MiscUtil.addCondition(lsSQL, "sSourceNo = " + SQLUtil.toSQL(fsSourceNo) + 
                                                " AND sSourceCd = "  + SQLUtil.toSQL(fsSourceCd));
        
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        try {
            if (loRS.next())
                return loadTransaction(loRS.getString("sTransNox"));
        } catch (SQLException ex) {
            psMessagex = ex.getMessage();
            System.err.println(psMessagex);
        }
        
        return false;
    }
    
    public boolean loadTransaction(String fsTransNox) {
        poData = poCtrl.loadTransaction(fsTransNox);
        
        if (poData.getTransNo()== null){
            psMessagex = poCtrl.getErrMsg() + "\n" + poCtrl.getMessage();
            System.err.println(psMessagex);
            return false;
        } else{           
            pnEditMode = EditMode.READY;
            return true;
        }
   }

    public boolean saveUpdate() {
        if(pnEditMode == EditMode.UNKNOWN){
            return false;
        }else{
            // Perform testing on values that needs approval here...
            UnitReceiptMaster loResult;
            if(pnEditMode == EditMode.ADDNEW)
                loResult = poCtrl.saveUpdate(poData, "");
            else loResult = poCtrl.saveUpdate(poData, (String) poData.getValue(1));

            if(loResult == null){
                psMessagex = poCtrl.getErrMsg() + "\n" + poCtrl.getMessage();
                System.err.println(psMessagex);
                return false;
            } else {
                pnEditMode = EditMode.READY;
                poData = loResult;
                return true;
            }
      }
   }

    public boolean deleteTransaction(String fsTransNox) {
        if(poCtrl == null){
            return false;
        } else if(pnEditMode != EditMode.READY){
            psMessagex = "Edit Mode does not allow deletion of transaction!";
            System.err.println(psMessagex);
            return false;
        }else{
            boolean lbResult = poCtrl.deleteTransaction(fsTransNox);
            if(lbResult){pnEditMode = EditMode.UNKNOWN;}     
            return lbResult;
        }
   }

    public boolean closeTransaction(String fsTransNox){return false;}

    public boolean postTransaction(String fsTransNox){return false;}

    public boolean voidTransaction(String fsTransNox){return false;}

    public boolean cancelTransaction(String fsTransNox){return false;}    
   
    public void setBranch(String fsBranchCD) {
        psBranchCd = fsBranchCD;
        poCtrl.setBranch(fsBranchCD);
    }

    public void setGRider(GRider foGRider) {
        this.poGRider = foGRider;
        poCtrl.setGRider(foGRider);
    }

    public int getEditMode() {
        return pnEditMode;
    }
    
    public void setClientID(String fsClientID){
        psClientID = fsClientID;
    }
    
    public void setORNumber(String fsORNumber){
        psORNumber = fsORNumber;
    }
    
    public void setVATRate(double fnVATRatex){
        pnVATRatex = fnVATRatex;
    }
    
    public void setTranTotl(double fnTranTotl){
        pnTranTotl = fnTranTotl;
    }
    
    public void setFreightCharge(double fnFreightx){
        pnFreightx = fnFreightx;
    }
    
    public void setDiscount(double fnDiscount){
        pnDiscount = fnDiscount;
    }
    
    public void setAddtnlDiscount(double fnAddDiscx){
        pnAddDiscx = fnAddDiscx;
    }
                   
    public boolean showReceipt(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(pxeReceipt));
            MainController loReceipt = new MainController();
            loReceipt.setGRider(poGRider);
            loReceipt.setClientID(psClientID);
            
            loReceipt.setVATRate(pnVATRatex);
            loReceipt.setTranTotl(pnTranTotl);
            loReceipt.setFreight(pnFreightx);
            loReceipt.setDiscount(pnDiscount);
            loReceipt.setAddtnlDiscount(pnAddDiscx);
            
            //set other payment data
            loReceipt.setUnitCreditCard(poCreditCard);
            loReceipt.setUnitCheckPayment(poCheck);
            loReceipt.setUnitGiftCert(poGiftCert);
            loReceipt.setUnitFinancer(poFinancer);
//            poFinancer.setFinAmtxx(pnTranTotl);
            //todo: set advance payment data
            
            psORNumber = MiscUtil.getNextCode("Receipt_Master", 
                                                "sORNumber", 
                                                false, 
                                                poGRider.getConnection(), 
                                                "", "sTransNox LIKE " + SQLUtil.toSQL(psBranchCd + System.getProperty("pos.clt.trmnl.no") + "%"));
            
            loReceipt.setORNumber(psORNumber);
            loReceipt.setSourceCd(poData.getSourceCd());

            fxmlLoader.setController(loReceipt);
            
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.getIcons().add(new Image(pxeStageIcon));
            stage.setTitle("Payment FX v1.0");
            stage.showAndWait();
            
            pnAmtPaidx = 0.00;
            boolean lbSuccess = false;
            if (!loReceipt.isCancelled()){
                setMaster("sORNumber", loReceipt.getORNumber());
                setMaster("nCashAmtx", loReceipt.getTenderedCash()); //loReceipt.getCashAmtx()
                setMaster("nVATSales", loReceipt.getVATableSales());
                setMaster("nVATAmtxx", loReceipt.getVATableAmntx());
                setMaster("nNonVATSl", loReceipt.getNonVATSl());
                setMaster("nZroVATSl", loReceipt.getZroVATSl());
                setMaster("nCWTAmtxx", loReceipt.getCWTAmtxx());

                lbSuccess = saveUpdate();
                
                if (!lbSuccess) return false;
                pnAmtPaidx = loReceipt.getCashAmtx();
                
                if (loReceipt.getCredtCrd() + loReceipt.getCheckPay() + loReceipt.getGiftCert() + loReceipt.getFinancer() > 0){
                    XMSalesPayment instance = new XMSalesPayment(poGRider, poGRider.getBranchCode(), true);
                    
                    if (loReceipt.getCredtCrd() > 0){
                        poCreditCard = loReceipt.getUnitCreditCard();
                        instance.newTransaction(PaymentType.CREDIT_CARD);
                        instance.setMaster("sSourceCd", "ORec");
                        instance.setMaster("sSourceNo", getMaster("sTransNox"));

                        instance.setPayInfo("sBranchCd", poGRider.getBranchCode());
                        instance.setPayInfo("sTermnlID", poCreditCard.getTermnlID());
                        instance.setPayInfo("sBankCode", poCreditCard.getBankCode());
                        instance.setPayInfo("sCardIDxx", poCreditCard.getCardIDxx());
                        instance.setPayInfo("sCardNoxx", poCreditCard.getCardNoxx());
                        instance.setPayInfo("sApprovNo", poCreditCard.getApprovNo());
                        instance.setPayInfo("sBatchNox", poCreditCard.getBatchNox());
                        instance.setPayInfo("nAmountxx", poCreditCard.getAmountxx());
                        instance.setPayInfo("sTermCode", poCreditCard.getTermCode());
                                    
                        lbSuccess =  instance.saveUpdate();
                        if (!lbSuccess) return false;
                        
                        pnAmtPaidx = pnAmtPaidx + (double) poCreditCard.getAmountxx();
                    }
                    
                    if (loReceipt.getCheckPay() > 0){
                        poCheck = loReceipt.getUnitCheckPayment();
                        instance.newTransaction(PaymentType.CHECK);
                        instance.setMaster("sSourceCd", "ORec");
                        instance.setMaster("sSourceNo", getMaster("sTransNox"));

                        instance.setPayInfo("dTransact", poGRider.getServerDate());
                        instance.setPayInfo("sBankCode", poCheck.getBankCode());
                        instance.setPayInfo("sCheckNox", poCheck.getCheckNox());
                        instance.setPayInfo("dCheckDte", poCheck.getCheckDte());
                        instance.setPayInfo("nAmountxx", poCheck.getAmountxx());
                        instance.setPayInfo("sRemarksx", poCheck.getRemarksx());
                        lbSuccess =  instance.saveUpdate();
                        if (!lbSuccess) return false;
                        
                        pnAmtPaidx = pnAmtPaidx + (double) poCheck.getAmountxx();
                    }

                    if (loReceipt.getGiftCert() > 0){
                        poGiftCert = loReceipt.getUnitGiftCert();
                        instance.newTransaction(PaymentType.GIFT_CERTIFICATE);
                        instance.setMaster("sSourceCd", "ORec");
                        instance.setMaster("sSourceNo", getMaster("sTransNox"));
                        
                        instance.setPayInfo("sCompnyCd", poGiftCert.getCompnyCd());
                        instance.setPayInfo("dValidity", poGiftCert.getValidity());
                        instance.setPayInfo("sReferNox", poGiftCert.getReferNox());
                        instance.setPayInfo("nAmountxx", poGiftCert.getAmountxx());
                        
                        if (pnTranTotl < Double.valueOf(poGiftCert.getAmtPaidx().toString()))
                            instance.setPayInfo("nAmtPaidx", pnTranTotl);
                        else
                            instance.setPayInfo("nAmtPaidx", poGiftCert.getAmtPaidx());
                        
                        instance.setPayInfo("sRemarksx", poGiftCert.getRemarksx());
                        lbSuccess =  instance.saveUpdate();
                        if (!lbSuccess) return false;
                        
                        pnAmtPaidx = pnAmtPaidx + (double) poGiftCert.getAmountxx();
                    }
                    
                    if (loReceipt.getFinancer() > 0){
                        poFinancer = loReceipt.getUnitFinancer();
                        instance.newTransaction(PaymentType.FINANCER);
                        instance.setMaster("sSourceCd", "ORec");
                        instance.setMaster("sSourceNo", getMaster("sTransNox"));

                        instance.setPayInfo("sClientID", poFinancer.getFinancer());
                        instance.setPayInfo("sReferNox", poFinancer.getReferNox());
                        instance.setPayInfo("nFinAmtxx", poFinancer.getFinAmtxx());
                        instance.setPayInfo("nAmtPaidx", poFinancer.getAmtPaidx());
                        instance.setPayInfo("sTermCode", poFinancer.getTermIDxx());
                        instance.setPayInfo("sRemarksx", poFinancer.getRemarksx());
                        lbSuccess =  instance.saveUpdate();
                        if (!lbSuccess) return false;
                        
                        pnAmtPaidx = pnAmtPaidx + (double) poFinancer.getAmtPaidx();
                    }
                }
                
                return lbSuccess;
            }   
        } catch (IOException e) {
            psMessagex = e.getMessage();
            System.err.println(psMessagex);
        }
        
        return false;
    }
   
    
    private ORMaster poCtrl;
    private UnitReceiptMaster poData;
    private UnitCreditCardTrans poCreditCard;
    private UnitCheckPaymentTrans poCheck;
    private UnitGCPaymentTrans poGiftCert;
    private UnitFinancerTrans poFinancer;
    
    private GRider poGRider;
    private String psBranchCd;
    private boolean pbWithParnt;
    private int pnEditMode;

    private String psClientID;
    private double pnVATRatex = 0.00;
    private double pnTranTotl = 0.00;
    private double pnDiscount = 0.00;
    private double pnAddDiscx = 0.00;
    private double pnFreightx = 0.00;
    
    private double pnAmtPaidx = 0.00;
    
    private String psMessagex;
    private String psORNumber;
       
    private final String pxeStageIcon = "org/rmj/payment/agent/images/64.png";
    private final String pxeReceipt = "iface/cash.fxml";
}
