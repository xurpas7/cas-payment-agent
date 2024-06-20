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
import org.rmj.payment.base.CreditCardTrans;
import org.rmj.payment.pojo.UnitCreditCardTrans;

public class XMCreditCardTrans{
    public XMCreditCardTrans(GRider foGRider, String fsBranchCd, boolean fbWithParent){
      this.poGRider = foGRider;
      if(foGRider != null){
         this.pbWithParnt = fbWithParent;
         this.psBranchCd = fsBranchCd;
         poControl = new CreditCardTrans();
         poControl.setGRider(foGRider);
         poControl.setBranch(psBranchCd);
         poControl.setWithParent(true);
         pnEditMode = EditMode.UNKNOWN;

         poData = new UnitCreditCardTrans();
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
        if(pnEditMode == EditMode.UNKNOWN || poControl == null)
            return null;
        else return poData.getValue(fnCol);
    }

    public Object getMaster(String fsCol) {
        return getMaster(poData.getColumn(fsCol));
    }

    public boolean newTransaction() {
        poData = poControl.newTransaction();
        
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
            lsSQL = MiscUtil.addCondition(poControl.getSQ_Master(), "sSourceNo = " + SQLUtil.toSQL(fsValue) + 
                                                                    " AND sSourceCd = " + SQLUtil.toSQL(fsSource));
        } else{
            if (poGRider.isMainOffice()){
                lsSQL = MiscUtil.addCondition(poControl.getSQ_Master(), "sTransNox = " + SQLUtil.toSQL(fsValue));
            } else{
                lsSQL = MiscUtil.addCondition(poControl.getSQ_Master(), "sTransNox LIKE " + SQLUtil.toSQL(psBranchCd + "%") + 
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
                poData = poControl.loadTransaction(loRS.getString("sTransNox"));
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
            UnitCreditCardTrans loResult;
            if(pnEditMode == EditMode.ADDNEW)
                loResult = poControl.saveUpdate(poData, "");
            else loResult = poControl.saveUpdate(poData, (String) poData.getValue(1));

            if(loResult == null){
                if (poControl.getErrMsg().isEmpty())
                    ShowMessageFX.Warning(poControl.getMessage(), pxeModuleName, "Please verify your entry.");
                else ShowMessageFX.Error(poControl.getErrMsg(), pxeModuleName, "Please inform MIS department.");
                return false;
            } else {
                pnEditMode = EditMode.READY;
                poData = loResult;
                return true;
            }
      }
   }

   public boolean deleteTransaction(String fsTransNox) {
        if(poControl == null){
            return false;
        } else if(pnEditMode != EditMode.READY){
            ShowMessageFX.Warning("Edit Mode does not allow deletion of transaction!", pxeModuleName, "Please verify your entry.");
            return false;
        }else{
            boolean lbResult = poControl.deleteTransaction(fsTransNox);
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
        poControl.setBranch(fsBranchCD);
    }

    public void setGRider(GRider foGRider) {
        this.poGRider = foGRider;
        poControl.setGRider(foGRider);
    }

    public int getEditMode() {
        return pnEditMode;
    }
   
    private UnitCreditCardTrans poData;
    private CreditCardTrans poControl;
    
    private GRider poGRider;
    private int pnEditMode;
    private String psBranchCd;
    private boolean pbWithParnt = false;
    
    private final String pxeModuleName = "org.rmj.payment.base.CreditCardTrans";
}