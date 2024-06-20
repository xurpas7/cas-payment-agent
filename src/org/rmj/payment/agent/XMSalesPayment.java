/**
 * @author  Michael Cuison
 * 
 * @since    2018-09-06
 */
package org.rmj.payment.agent;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.MiscUtil;
import org.rmj.appdriver.SQLUtil;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.payment.base.SalesPayment;
import org.rmj.payment.agent.constant.PaymentType;
import org.rmj.payment.pojo.UnitSalesPayment;

public class XMSalesPayment{
    public XMSalesPayment(GRider foGRider, String fsBranchCd, boolean fbWithParent){
      this.poGRider = foGRider;
      if(foGRider != null){
         this.pbWithParnt = fbWithParent;
         this.psBranchCd = fsBranchCd;
         poCtrl = new SalesPayment();
         poCtrl.setGRider(foGRider);
         poCtrl.setBranch(psBranchCd);
         poCtrl.setWithParent(fbWithParent);
         pnEditMode = EditMode.UNKNOWN;

         poData = new UnitSalesPayment();
      }
    }

    public void setMaster(int fnCol, Object foData) {
        if (pnEditMode != EditMode.UNKNOWN){
            // Don't allow specific fields to assign values
            if(!(fnCol == poData.getColumn("sTransNox") ||
                fnCol == poData.getColumn("nAmountxx") ||
                fnCol == poData.getColumn("cPaymForm") ||
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

    public boolean newTransaction(String lsPaymForm) {
        poData = poCtrl.newTransaction();
        
        if (poData == null){
            return false;
        }else{
            poData.setPaymForm(lsPaymForm);
            
            switch (lsPaymForm) {
                case PaymentType.CHECK:
                    poCheck = new XMCheckPaymentTrans(poGRider, psBranchCd, pbWithParnt);
                    if (!poCheck.newTransaction()) return false;
                    break;
                case PaymentType.CREDIT_CARD:
                    poCreditCard = new XMCreditCardTrans(poGRider, psBranchCd, pbWithParnt);
                    if (!poCreditCard.newTransaction()) return false;
                    break;
                case PaymentType.GIFT_CERTIFICATE:
                    poGiftCheck = new XMGCPaymentTrans(poGRider, psBranchCd, pbWithParnt);
                    if (!poGiftCheck.newTransaction()) return false;
                    break;
                case PaymentType.FINANCER:
                    poFinance = new XMFinancerTrans(poGRider, psBranchCd, pbWithParnt);
                    if (!poFinance.newTransaction()) return false;
                    break;
                default:
                    return false;
            }
            
            pnEditMode = EditMode.ADDNEW;
            return true;
        }
    }
    
    private String getSQ_Payment(){
        return "SELECT" +
                    "  sTransNox" +
                    ", CASE cPaymForm" +
                            " WHEN '1' THEN 'CHECK'" +
                            " WHEN '2' THEN 'CREDIT_CARD'" +
                            " WHEN '3' THEN 'GIFT CHECK'" +
                            " WHEN '4' THEN 'FINANCE'" +
                        " END cPaymForm" +
                    ", nAmountxx" +
                    ", sSourceCd" +
                    ", sSourceNo" +
                    ", dModified" +
                " FROM Sales_Payment";
    }
    
    public boolean loadCreditCard(String fsValue, String fsSource){
        String lsOldProc = pxeModuleName + ".loadCreditCard";
        String lsSQL = MiscUtil.addCondition(getSQ_Payment(), 
                                                "cPaymForm = " + SQLUtil.toSQL(PaymentType.CREDIT_CARD));
        
        psBranchCd = poGRider.getBranchCode();
        if (!fsSource.equals("")){
            lsSQL = MiscUtil.addCondition(lsSQL, "sSourceNo = " + SQLUtil.toSQL(fsValue) + 
                                                    " AND sSourceCd = " + SQLUtil.toSQL(fsSource));
        } else{
            if (poGRider.isMainOffice()){
                lsSQL = MiscUtil.addCondition(lsSQL, "sTransNox = " + SQLUtil.toSQL(fsValue));
            } else{
                lsSQL = MiscUtil.addCondition(lsSQL, "sTransNox LIKE " + SQLUtil.toSQL(psBranchCd + "%") + 
                                                        " AND sTransNox = " + SQLUtil.toSQL(fsValue));
            }
        }
        
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        try {
            if (loRS.next())
                poData = poCtrl.loadTransaction(loRS.getString("sTransNox"));
            else
                return false;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            ShowMessageFX.Error("Unable to load transaction [" + fsValue + ", " + fsSource + "]", lsOldProc, "Please inform MIS Department.");
            return false;
        }
        
        if (poData.getTransNo() == null){
            System.out.println("No transaction loaded [" + fsValue + ", " + fsSource + "]" + " for " + lsOldProc);
            return false;
        }
        
        poCreditCard = new XMCreditCardTrans(poGRider, psBranchCd, pbWithParnt);
        poCreditCard.loadTransaction(poData.getTransNo(), pxeSourceCode);
        
        if (poCreditCard == null) return false;

        pnEditMode = EditMode.READY;
        return true;
    }
    
    public boolean loadCheck(String fsValue, String fsSource){
        String lsOldProc = pxeModuleName + ".loadCheck";
        String lsSQL = MiscUtil.addCondition(getSQ_Payment(), "cPaymForm = '1'");
        
        psBranchCd = poGRider.getBranchCode();
        if (!fsSource.equals("")){
            lsSQL = MiscUtil.addCondition(lsSQL, "sSourceNo = " + SQLUtil.toSQL(fsValue) + 
                                                    " AND sSourceCd = " + SQLUtil.toSQL(fsSource));
        } else{
            if (poGRider.isMainOffice()){
                lsSQL = MiscUtil.addCondition(lsSQL, "sTransNox = " + SQLUtil.toSQL(fsValue));
            } else{
                lsSQL = MiscUtil.addCondition(lsSQL, "sTransNox LIKE " + SQLUtil.toSQL(psBranchCd + "%") + 
                                                        " AND sTransNox = " + SQLUtil.toSQL(fsValue));
            }
        }
        
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        try {
            if (loRS.next())
                poData = poCtrl.loadTransaction(loRS.getString("sTransNox"));
            else
                return false;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            ShowMessageFX.Error("Unable to load transaction [" + fsValue + ", " + fsSource + "]", lsOldProc, "Please inform MIS Department.");
            return false;
        }
        
        if (poData.getTransNo() == null){
            System.out.println("No transaction loaded [" + fsValue + ", " + fsSource + "]" + " for " + lsOldProc);
            return false;
        }
        
        poCheck = new XMCheckPaymentTrans(poGRider, psBranchCd, pbWithParnt);
        poCheck.loadTransaction(poData.getTransNo(), pxeSourceCode);
        
        if (poCheck == null) return false;

        pnEditMode = EditMode.READY;
        return true;
    }
    
    public boolean loadGC(String fsValue, String fsSource){
        String lsOldProc = pxeModuleName + ".loadGC";
        String lsSQL = MiscUtil.addCondition(getSQ_Payment(), "cPaymForm = '3'");
        
        psBranchCd = poGRider.getBranchCode();
        if (!fsSource.equals("")){
            lsSQL = MiscUtil.addCondition(lsSQL, "sSourceNo = " + SQLUtil.toSQL(fsValue) + 
                                                    " AND sSourceCd = " + SQLUtil.toSQL(fsSource));
        } else{
            if (poGRider.isMainOffice()){
                lsSQL = MiscUtil.addCondition(lsSQL, "sTransNox = " + SQLUtil.toSQL(fsValue));
            } else{
                lsSQL = MiscUtil.addCondition(lsSQL, "sTransNox LIKE " + SQLUtil.toSQL(psBranchCd + "%") + 
                                                        " AND sTransNox = " + SQLUtil.toSQL(fsValue));
            }
        }
        
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        try {
            if (loRS.next())
                poData = poCtrl.loadTransaction(loRS.getString("sTransNox"));
            else
                return false;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            ShowMessageFX.Error("Unable to load transaction [" + fsValue + ", " + fsSource + "]", lsOldProc, "Please inform MIS Department.");
            return false;
        }
        
        if (poData.getTransNo() == null){
            System.out.println("No transaction loaded [" + fsValue + ", " + fsSource + "]" + " for " + lsOldProc);
            return false;
        }
        
        poGiftCheck = new XMGCPaymentTrans(poGRider, psBranchCd, pbWithParnt);
        poGiftCheck.loadTransaction(poData.getTransNo(), pxeSourceCode);
        
        if (poGiftCheck == null) return false;

        pnEditMode = EditMode.READY;
        return true;
    }
    
    public boolean loadFinance(String fsValue, String fsSource){
        String lsOldProc = pxeModuleName + ".loadFinance";
        String lsSQL = MiscUtil.addCondition(getSQ_Payment(), 
                                                "cPaymForm = " + SQLUtil.toSQL(PaymentType.FINANCER));
        
        psBranchCd = poGRider.getBranchCode();
        if (!fsSource.equals("")){
            lsSQL = MiscUtil.addCondition(lsSQL, "sSourceNo = " + SQLUtil.toSQL(fsValue) + 
                                                    " AND sSourceCd = " + SQLUtil.toSQL(fsSource));
        } else{
            if (poGRider.isMainOffice()){
                lsSQL = MiscUtil.addCondition(lsSQL, "sTransNox = " + SQLUtil.toSQL(fsValue));
            } else{
                lsSQL = MiscUtil.addCondition(lsSQL, "sTransNox LIKE " + SQLUtil.toSQL(psBranchCd + "%") + 
                                                        " AND sTransNox = " + SQLUtil.toSQL(fsValue));
            }
        }
        
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        try {
            if (loRS.next())
                poData = poCtrl.loadTransaction(loRS.getString("sTransNox"));
            else
                return false;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            ShowMessageFX.Error("Unable to load transaction [" + fsValue + ", " + fsSource + "]", lsOldProc, "Please inform MIS Department.");
            return false;
        }
        
        if (poData.getTransNo() == null){
            System.out.println("No transaction loaded [" + fsValue + ", " + fsSource + "]" + " for " + lsOldProc);
            return false;
        }
        
        poFinance = new XMFinancerTrans(poGRider, psBranchCd, pbWithParnt);
        poFinance.loadTransaction(poData.getTransNo(), pxeSourceCode);
        
        if (poFinance == null) return false;

        pnEditMode = EditMode.READY;
        return true;
    }
    
    public boolean saveUpdate() {
        if(pnEditMode == EditMode.UNKNOWN){
            return false;
        }else{
            // Perform testing on values that needs approval here...
            UnitSalesPayment loResult;
            if(pnEditMode == EditMode.ADDNEW){
                if (poData.getPaymForm().equals(PaymentType.FINANCER)){
                    poData.setAmountxx((Number) getPayInfo("nAmtPaidx"));
                } else {
                    poData.setAmountxx((Number) getPayInfo("nAmountxx"));
                }
                
                loResult = poCtrl.saveUpdate(poData, "");
            } else loResult = poCtrl.saveUpdate(poData, (String) poData.getValue(1));

            if(loResult == null){
                if (poCtrl.getErrMsg().isEmpty())
                    ShowMessageFX.Warning(poCtrl.getMessage(), pxeModuleName, "Please verify your entry.");
                else ShowMessageFX.Error(poCtrl.getErrMsg(), pxeModuleName, "Please inform MIS department.");
                return false;
            } else {
                //save payment form
                if (poData.getPaymForm().equals(PaymentType.CHECK)){
                    if (poCheck != null){
                        poCheck.setMaster("sSourceNo", loResult.getTransNo());
                        poCheck.setMaster("sSourceCd", pxeSourceCode);

                        if (!poCheck.saveUpdate()) return false;
                    }
                }
                
                if (poData.getPaymForm().equals(PaymentType.CREDIT_CARD)){
                    if (poCreditCard != null){
                        poCreditCard.setMaster("sSourceNo", loResult.getTransNo());
                        poCreditCard.setMaster("sSourceCd", pxeSourceCode);

                        if (!poCreditCard.saveUpdate()) return false;
                    }
                }
                
                if (poData.getPaymForm().equals(PaymentType.GIFT_CERTIFICATE)){
                    if (poGiftCheck != null){
                        poGiftCheck.setMaster("sSourceNo", loResult.getTransNo());
                        poGiftCheck.setMaster("sSourceCd", pxeSourceCode);

                        if (!poGiftCheck.saveUpdate()) return false;
                    }
                }
                
                if (poData.getPaymForm().equals(PaymentType.FINANCER)){
                    if (poFinance != null){
                        poFinance.setMaster("sSourceNo", loResult.getTransNo());
                        poFinance.setMaster("sSourceCd", pxeSourceCode);

                        if (!poFinance.saveUpdate()) return false;
                    }
                }
                
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
            ShowMessageFX.Warning("Edit Mode does not allow deletion of transaction!", pxeModuleName, "Please verify your entry.");
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

    public void setPayInfo(String fsCol, Object foData){
        if (pnEditMode != EditMode.UNKNOWN){
            if (!(fsCol.equals("sSourceCD") ||
                    fsCol.equals("sSourceNo"))){
                switch (poData.getPaymForm()) {
                    case PaymentType.CHECK:
                        poCheck.setMaster(fsCol, foData);
                        break;
                    case PaymentType.CREDIT_CARD:
                        poCreditCard.setMaster(fsCol, foData);
                        break;
                    case PaymentType.GIFT_CERTIFICATE:
                        poGiftCheck.setMaster(fsCol, foData);
                        break;
                    case PaymentType.FINANCER:
                        poFinance.setMaster(fsCol, foData);
                        break;
                    default:
                        break;
                }
            }
        }
    }
    
    public Object getCheckInfo(String fsCol){
        if (pnEditMode == EditMode.UNKNOWN) return null;
        if (poCheck == null) return null;
        
        return poCheck.getMaster(fsCol);
    }
    
    public Object getCreditCardInfo(String fsCol){
        if (pnEditMode == EditMode.UNKNOWN) return null;
        if (poCreditCard == null) return null;
        
        return poCreditCard.getMaster(fsCol);
    }
    
    public Object getGCInfo(String fsCol){
        if (pnEditMode == EditMode.UNKNOWN) return null;
        if (poGiftCheck == null) return null;
        
        return poGiftCheck.getMaster(fsCol);
    }
    
    public Object getFinanceInfo(String fsCol){
        if (pnEditMode == EditMode.UNKNOWN) return null;
        if (poFinance == null) return null;
        
        return poFinance.getMaster(fsCol);
    }
    
    public Object getPayInfo(String fsCol) {
        if(pnEditMode == EditMode.UNKNOWN)
            return null;
        else{
            switch (poData.getPaymForm()) {
                case PaymentType.CHECK:
                    return poCheck.getMaster(fsCol);
                case PaymentType.CREDIT_CARD:
                    return poCreditCard.getMaster(fsCol);
                case PaymentType.GIFT_CERTIFICATE:
                    return poGiftCheck.getMaster(fsCol);
                case PaymentType.FINANCER:
                    return poFinance.getMaster(fsCol);
                default:
                    return null;
            }
        } 
    }
       
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
    
    private XMCreditCardTrans poCreditCard;
    private XMCheckPaymentTrans poCheck;
    private XMGCPaymentTrans poGiftCheck;
    private XMFinancerTrans poFinance;
    
    private UnitSalesPayment poData;
    private SalesPayment poCtrl;
    private GRider poGRider;
    private int pnEditMode;
    private String psBranchCd;
    private boolean pbWithParnt = false;
    
    private final String pxeModuleName = "XMSalesPayment";
    private final String pxeSourceCode = "SlPy";
}
