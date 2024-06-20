package org.rmj.payment.agent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.MiscUtil;
import org.rmj.appdriver.SQLUtil;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.constants.CRMEvent;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.payment.agent.iface.CashPulloutFX;
import org.rmj.payment.base.CashDrawer;
import org.rmj.payment.pojo.UnitCashDrawer;

public class XMCashDrawer {
    public XMCashDrawer(GRider foGRider, String fsBranchCd, boolean fbWithParent, boolean fbWithEntry){
        poApp = foGRider;
        psBranchCd = fsBranchCd;
        pbWithEntry = fbWithEntry;
        pbWithParent = fbWithParent;
        
        poCtrl = new CashDrawer();
        poCtrl.setGRider(foGRider);
        poCtrl.setBranch(fsBranchCd);
    }
    
    public boolean NewTransaction(){
        if (poApp == null) {
            setMessage("Application driver is not set.");
            return false;
        }
        
        poData = poCtrl.newTransaction();
        
        if (poData == null){
            setMessage(poCtrl.getErrMsg() + "\n" +  poCtrl.getMessage());
            return false;
        }else{
            pnEditMode = EditMode.ADDNEW;
            return true;
        }
    }
    
    public boolean SaveTransaction() {
        if(pnEditMode == EditMode.UNKNOWN){
            return false;
        }else{
            // Perform testing on values that needs approval here...
            UnitCashDrawer loResult;            
            if(pnEditMode == EditMode.ADDNEW)
                loResult = poCtrl.saveUpdate(poData, "");
            else loResult = poCtrl.saveUpdate(poData, (String) poData.getValue(1));

            if(loResult == null){
                setMessage(poCtrl.getErrMsg() + "\n" + poCtrl.getMessage());
                System.err.println(getMessage());
                return false;
            } else {
                pnEditMode = EditMode.READY;
                poData = loResult;
                return true;
            }
        }
    }
    
    public boolean showGUI(){
        if (poData == null) return false;
        
        try {
            CashPulloutFX instance = new CashPulloutFX();
            
            double lnTotCash = Double.valueOf(String.valueOf(poData.getCashAmount())) + 
                                Double.valueOf(String.valueOf(poData.getOpeningBalance())) - 
                                Double.valueOf(String.valueOf(poData.getWithdrawAmount()));
            
            instance.setOpeningBalance(poData.getOpeningBalance());
            instance.setCashAmount(lnTotCash);
            instance.setCheckAmount(poData.getCheckAmount());
            instance.setChargeAmount(poData.getChargeAmount());
            instance.setCreditCardAmount(poData.getCreditCardAmount());
            instance.setGiftCertAmount(poData.getGiftCertAmount());
            instance.setFinanceAmount(poData.getFinanceAmount());
            instance.setWithrawal(poData.getWithdrawAmount());
            instance.setDeposit(poData.getDepositAmount());
            
            CommonUtils.showModal(instance);
            
            if (!instance.isCancelled()){
                if ((double) instance.getCashPullout() > 0.00){
                    //todo: insert of cash pull out
                    String lsSQL = "UPDATE Cash_Drawer SET" +
                                        "  nWithdraw = nWithdraw + " + (double) instance.getCashPullout() +
                                    " WHERE sTrandate = " + SQLUtil.toSQL(poData.getTranDate()) +
                                        " AND sCashierx = " + SQLUtil.toSQL(poData.getCashier());
                    
                    if (poApp.executeQuery(lsSQL, "Cash_Drawer", psBranchCd, "") <= 0){
                        if (!poApp.getErrMsg().isEmpty()){
                            ShowMessageFX.Error(poApp.getMessage(), "Warning", "Exception Detected");
                            return false;
                        }
                    }
                    
                    lsSQL = "UPDATE Daily_Summary SET" +
                                "  nCPullOut = nCPullOut + " + (double) instance.getCashPullout() +
                            " WHERE sTrandate = " + SQLUtil.toSQL(poData.getTranDate()) +
                                " AND sCashierx = " + SQLUtil.toSQL(poData.getCashier()) +
                                " AND sCRMNumbr = " + SQLUtil.toSQL(System.getProperty("pos.clt.crm.no"));
                    
                    if (poApp.executeQuery(lsSQL, "Daily_Summary", psBranchCd, "") <= 0){
                        if (!poApp.getErrMsg().isEmpty()){
                            ShowMessageFX.Error(poApp.getMessage(), "Warning", "Exception Detected");
                            return false;
                        }
                    }
                    
                    CommonUtils.createEventLog(poApp, poApp.getBranchCode() + System.getProperty("pos.clt.trmnl.no"), CRMEvent.CASH_PULLOUT, String.valueOf(instance.getCashPullout()), System.getProperty("pos.clt.crm.no"));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(XMCashDrawer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }
    
    public boolean LoadCashierTransaction(String fsTranDate, String fsCashierx){
        String lsSQL = MiscUtil.makeSelect(new UnitCashDrawer());
        
        lsSQL = MiscUtil.addCondition(lsSQL, "sTranDate = " + SQLUtil.toSQL(fsTranDate) + 
                                                " AND sCashierx = " + SQLUtil.toSQL(fsCashierx) + 
                                                " AND cTranStat = '0'");
        
        ResultSet loRS = poApp.executeQuery(lsSQL);
        
        try {
            if (loRS.next())
                return LoadTransaction(loRS.getString("sTransNox"));
        } catch (SQLException ex) {
            setMessage(ex.getMessage());
            System.err.println(getMessage());
        }
        
        return false;
    }
    
    public boolean LoadTransaction(String fsTransNox) {
        poData = poCtrl.loadTransaction(fsTransNox);
        
        if (poData.getTransactionNo()== null){
            setMessage(poCtrl.getErrMsg() + "\n" + poCtrl.getMessage());
            System.err.println(getMessage());
            return false;
        } else{           
            pnEditMode = EditMode.READY;
            return true;
        }
    }
    
    public boolean UpdateTransaction(){
        if (poData == null) {
            setMessage("No record to update.");
            return false;
        }
        
        pnEditMode = EditMode.UPDATE;
        return true;
    }
    
    public boolean DeleteTransaction(String fsTransNox){return false;}
    
    public boolean CloseTransaction(String fsTransNox){return false;}

    public boolean PostTransaction(String fsTransNox){return false;}

    public boolean VoidTransaction(String fsTransNox){return false;}

    public boolean CancelTransaction(String fsTransNox){return false;} 
    
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
    
    public void setMessage(String fsValue){
        psMessage = fsValue;
    }
    public String getMessage(){
        return psMessage;
    }
    
    private UnitCashDrawer poData;
    private CashDrawer poCtrl;
    
    private GRider poApp;
    private String psBranchCd;
    private boolean pbWithParent;
    private boolean pbWithEntry;
    private int pnEditMode;
    private String psMessage;
    
}
