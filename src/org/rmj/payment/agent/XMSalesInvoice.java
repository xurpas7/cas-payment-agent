/**
 * @author  Michael Cuison
 * 
 * @since    2018-09-06
 */
package org.rmj.payment.agent;

import org.rmj.appdriver.constants.EditMode;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.payment.base.SalesInvoice;
import org.rmj.payment.pojo.UnitSalesInvoice;

public class XMSalesInvoice{
    public XMSalesInvoice(GRider foGRider, String fsBranchCd, boolean fbWithParent){
      this.poGRider = foGRider;
      if(foGRider != null){
         this.pbWithParnt = fbWithParent;
         this.psBranchCd = fsBranchCd;
         poCtrl = new SalesInvoice();
         poCtrl.setGRider(foGRider);
         poCtrl.setBranch(psBranchCd);
         poCtrl.setWithParent(true);
         pnEditMode = EditMode.UNKNOWN;

         poData = new UnitSalesInvoice();
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

   public boolean loadTransaction(String fsTransNox) {
        poData = poCtrl.loadTransaction(fsTransNox);
        
        if (poData.getTransNo()== null){
            ShowMessageFX.Warning("Unable to load transaction[" + fsTransNox + "]", pxeModuleName, "Please verify your entry.");
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
            UnitSalesInvoice loResult;
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
   
    private UnitSalesInvoice poData;
    private SalesInvoice poCtrl;
    private GRider poGRider;
    private int pnEditMode;
    private String psBranchCd;
    private boolean pbWithParnt = false;
    
    private final String pxeModuleName = "org.rmj.payment.base.SalesInvoice";
}
