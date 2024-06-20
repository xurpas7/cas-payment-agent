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
import org.rmj.payment.base.GCPaymentTrans;
import org.rmj.payment.pojo.UnitGCPaymentTrans;

public class XMGCPaymentTrans{
    public XMGCPaymentTrans(GRider foGRider, String fsBranchCd, boolean fbWithParent){
      this.poGRider = foGRider;
      if(foGRider != null){
         this.pbWithParnt = fbWithParent;
         this.psBranchCd = fsBranchCd;
         poCtrl = new GCPaymentTrans();
         poCtrl.setGRider(foGRider);
         poCtrl.setBranch(psBranchCd);
         poCtrl.setWithParent(true);
         pnEditMode = EditMode.UNKNOWN;

         poData = new UnitGCPaymentTrans();
      }
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
        
        if (poData == null){
            return false;
        }else{
            
            pnEditMode = EditMode.ADDNEW;
            return true;
        }
    }

    public boolean loadTransaction(String fsValue, String fsSource) {
        String lsSQL = "";
        psBranchCd = poGRider.getBranchCode();
       
        if (!fsSource.equals("")){
            lsSQL = MiscUtil.addCondition(poCtrl.getSQ_Master(), "sSourceNo = " + SQLUtil.toSQL(fsValue) + 
                                                                    " AND sSourceCd = " + SQLUtil.toSQL(fsSource));
        } else{
            if (poGRider.isMainOffice()){
                lsSQL = MiscUtil.addCondition(poCtrl.getSQ_Master(), "sTransNox = " + SQLUtil.toSQL(fsValue));
            } else{
                lsSQL = MiscUtil.addCondition(poCtrl.getSQ_Master(), "sTransNox LIKE " + SQLUtil.toSQL(psBranchCd + "%") + 
                                                                        " AND sTransNox = " + SQLUtil.toSQL(fsValue));
            }
        }
        
        try {
            ResultSet loRS = poGRider.executeQuery(lsSQL);
        
            if (MiscUtil.RecordCount(loRS) == 0){
                ShowMessageFX.Warning("No transaction detected for [" + fsValue + ", " + fsSource + "]", pxeModuleName, "Please verify your entry.");
                return false;
            } else{
                loRS.first();
                poData = poCtrl.loadTransaction(loRS.getString("sTransNox"));
            } 
        } catch (SQLException e) {
            ShowMessageFX.Warning(e.getMessage(), pxeModuleName, "Please inform MIS Department.");
        }
       
        if (poData.getTransNo()== null){
            ShowMessageFX.Warning("Unable to load transaction [" + fsValue + ", " + fsSource + "]", pxeModuleName, "Please verify your entry.");
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
            UnitGCPaymentTrans loResult;
            if(pnEditMode == EditMode.ADDNEW)
                loResult = poCtrl.saveUpdate(poData, "");
            else loResult = poCtrl.saveUpdate(poData, (String) poData.getValue(1));

            if(loResult == null){
                if (poCtrl.getErrMsg().isEmpty())
                    ShowMessageFX.Warning(poCtrl.getMessage(), pxeModuleName, "Please verify your entry.");
                else ShowMessageFX.Error(poCtrl.getErrMsg(), pxeModuleName, "Please inform MIS department.");
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
   
    private UnitGCPaymentTrans poData;
    private GCPaymentTrans poCtrl;
    private GRider poGRider;
    private int pnEditMode;
    private String psBranchCd;
    private boolean pbWithParnt = false;
    
    private final String pxeModuleName = "org.rmj.payment.base.GCPaymentTrans";
}
