/**
 * @author  Michael Cuison
 * 
 * @since    2018-09-06
 */
package org.rmj.payment.agent;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONObject;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.MiscUtil;
import org.rmj.appdriver.SQLUtil;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.agentfx.ui.showFXDialog;
import org.rmj.appdriver.constants.RecordStatus;
import org.rmj.payment.base.CreditCardTransMulti;
import org.rmj.payment.pojo.UnitCreditCardTrans;

public class XMCreditCardTransMulti{
    public XMCreditCardTransMulti(GRider foGRider, String fsBranchCd, boolean fbWithParent){
      this.poGRider = foGRider;
      if(foGRider != null){
         this.pbWithParnt = fbWithParent;
         this.psBranchCd = fsBranchCd;
         poControl = new CreditCardTransMulti();
         poControl.setGRider(foGRider);
         poControl.setBranch(psBranchCd);
         poControl.setWithParent(true);
         pnEditMode = EditMode.UNKNOWN;
      }
    }

//    public void setMaster(int fnCol, Object foData) {
//        if (pnEditMode != EditMode.UNKNOWN){
//            // Don't allow specific fields to assign values
//            if(!(fnCol == poData.getColumn("sTransNox") ||
//                fnCol == poData.getColumn("dModified"))){
//                
//                poData.setValue(fnCol, foData);
//            }
//        }
//    }
//
//    public void setMaster(String fsCol, Object foData) {
//        setMaster(poData.getColumn(fsCol), foData);
//    }

    

//    public boolean newTransaction() {
//        poData = poCtrl.newTransaction();
//        
//        if (poData == null){
//            return false;
//        }else{
//            
//            pnEditMode = EditMode.ADDNEW;
//            return true;
//        }
//    }

//    public boolean loadTransaction(String fsValue, String fsSource) {
//        String lsSQL = "";
//        psBranchCd = poGRider.getBranchCode();
//       
//        if (!fsSource.equals("")){
//            lsSQL = MiscUtil.addCondition(poCtrl.getSQ_Master(), "sSourceNo = " + SQLUtil.toSQL(fsValue) + 
//                                                                    " AND sSourceCd = " + SQLUtil.toSQL(fsSource));
//        } else{
//            if (poGRider.isMainOffice()){
//                lsSQL = MiscUtil.addCondition(poCtrl.getSQ_Master(), "sTransNox = " + SQLUtil.toSQL(fsValue));
//            } else{
//                lsSQL = MiscUtil.addCondition(poCtrl.getSQ_Master(), "sTransNox LIKE " + SQLUtil.toSQL(psBranchCd + "%") + 
//                                                                        " AND sTransNox = " + SQLUtil.toSQL(fsValue));
//           }
//        }
//        
//        try {
//            ResultSet loRS = poGRider.executeQuery(lsSQL);
//        
//            if (MiscUtil.RecordCount(loRS) == 0){
//                ShowMessageFX.Warning("No transaction detected for [" + fsValue + ", " + fsSource + "]", pxeModuleName, "Please verify your entry.");
//                return false;
//            } else{
//                loRS.first();
//                poData = poCtrl.loadTransaction(loRS.getString("sTransNox"));
//            } 
//        } catch (SQLException e) {
//            ShowMessageFX.Warning(e.getMessage(), pxeModuleName, "Please inform MIS Department.");
//        }
//       
//        if (poData.getTransNo()== null){
//            ShowMessageFX.Warning("Unable to load transaction [" + fsValue + ", " + fsSource + "]", pxeModuleName, "Please verify your entry.");
//            return false;
//        } else{           
//            pnEditMode = EditMode.READY;
//            return true;
//        }
//    }

//    public boolean saveUpdate() {
//        if(pnEditMode == EditMode.UNKNOWN){
//            return false;
//        }else{
//            // Perform testing on values that needs approval here...
//            UnitCreditCardTrans loResult;
//            if(pnEditMode == EditMode.ADDNEW)
//                loResult = poCtrl.saveUpdate(poData, "");
//            else loResult = poCtrl.saveUpdate(poData, (String) poData.getValue(1));
//
//            if(loResult == null){
//                if (poCtrl.getErrMsg().isEmpty())
//                    ShowMessageFX.Warning(poCtrl.getMessage(), pxeModuleName, "Please verify your entry.");
//                else ShowMessageFX.Error(poCtrl.getErrMsg(), pxeModuleName, "Please inform MIS department.");
//                return false;
//            } else {
//                pnEditMode = EditMode.READY;
//                poData = loResult;
//                return true;
//            }
//      }
//   }

//   public boolean deleteTransaction(String fsTransNox) {
//        if(poCtrl == null){
//            return false;
//        } else if(pnEditMode != EditMode.READY){
//            ShowMessageFX.Warning("Edit Mode does not allow deletion of transaction!", pxeModuleName, "Please verify your entry.");
//            return false;
//        }else{
//            boolean lbResult = poCtrl.deleteTransaction(fsTransNox);
//            if(lbResult){pnEditMode = EditMode.UNKNOWN;}     
//            return lbResult;
//        }
//   }

    public boolean closeTransaction(String fsTransNox){return false;}

    public boolean postTransaction(String fsTransNox){return false;}

    public boolean voidTransaction(String fsTransNox){return false;}

    public boolean cancelTransaction(String fsTransNox){return false;}    
    
    public JSONObject SearchDetail(int fnRow, int fnCol, String fsValue, boolean fbSearch, boolean fbByCode){
        String lsHeader = "";
        String lsColName = "";
        String lsColCrit = "";
        String lsSQL = "";
        JSONObject loJSON;
        ResultSet loRS;
        int lnRow;
        switch(fnCol){
            case 3:
                lsHeader = "Order No»Branch»Date»Total»Inv. Type»Supplier»Code";
                lsColName = "sTransNox»sBranchNm»dTransact»nTranTotl»xDescript»sClientNm»sInvTypCd";
                lsColCrit = "a.sTransNox»b.sBranchNm»a.dTransact»a.nTranTotl»c.sDescript»d.sClientNm»a.sInvTypCd";
//                lsSQL = getSQ_Purchases();
                
                if (fbByCode){
                    lsSQL = MiscUtil.addCondition(lsSQL, "a.sTransNox = " + SQLUtil.toSQL(fsValue));
                
                    loRS = poGRider.executeQuery(lsSQL);
                    
                    loJSON = showFXDialog.jsonBrowse(poGRider, loRS, lsHeader, lsColName);
                }
                else {
                    loJSON = showFXDialog.jsonSearch(poGRider, 
                                                        lsSQL, 
                                                        fsValue, 
                                                        lsHeader, 
                                                        lsColName, 
                                                        lsColCrit, 
                                                        fbSearch ? 6 : 0);
                }
                
                if (loJSON != null){
                    setDetail(fnRow, fnCol, (String) loJSON.get("sTransNox"));
//                    loadOrder((String) loJSON.get("sTransNox"));
                    return loJSON;
                } else{
                    setDetail(fnRow, fnCol, "");
                    return null;
                }
            case 4:
            case 5:
                lsHeader = "Brand»Description»Unit»Model»Inv. Type»Barcode»Stock ID";
                lsColName = "xBrandNme»sDescript»sMeasurNm»xModelNme»xInvTypNm»sBarCodex»sStockIDx";
                lsColCrit = "b.sDescript»a.sDescript»f.sMeasurNm»c.sDescript»d.sDescript»a.sBarCodex»a.sStockIDx";
                
                if (getDetail(fnRow, "sOrderNox").equals(""))
                      lsSQL="";
//                    lsSQL = MiscUtil.addCondition(getSQ_Inventory(), "a.cRecdStat = " + SQLUtil.toSQL(RecordStatus.ACTIVE));
                else 
                      lsSQL="";
//                    lsSQL = MiscUtil.addCondition(getSQ_Stocks((String) getDetail(fnRow, "sOrderNox")), "a.cRecdStat = " + SQLUtil.toSQL(RecordStatus.ACTIVE));
                
                if (fbByCode){
                    lsSQL = MiscUtil.addCondition(lsSQL, "a.sStockIDx = " + SQLUtil.toSQL(fsValue));
                    
                    loRS = poGRider.executeQuery(lsSQL);
                    
                    loJSON = showFXDialog.jsonBrowse(poGRider, loRS, lsHeader, lsColName);
                }else {
                    loJSON = showFXDialog.jsonSearch(poGRider, 
                                                        lsSQL, 
                                                        fsValue, 
                                                        lsHeader, 
                                                        lsColName, 
                                                        lsColCrit, 
                                                        fbSearch ? 1 : 5);
                }
                                
                if (loJSON != null){
                    setDetail(fnRow, fnCol, (String) loJSON.get("sStockIDx"));
                    //delete the barcode and descript on temp table
                    setDetail(fnRow, 100, "");
                    setDetail(fnRow, 101, "");
                    setDetail(fnRow, 102, "");
                    
                    if (fnCol == 4) setDetail(fnRow, "nUnitPrce", Double.valueOf((String)loJSON.get("nUnitPrce")));
                    if (loJSON.get("nQuantity") != null) setDetail(fnRow, 7, Integer.valueOf((String)loJSON.get("nQuantity")));
                    return loJSON;
                } else{
                    setDetail(fnRow, fnCol, "");
                    //delete the barcode and descript on temp table
                    setDetail(fnRow, 100, "");
                    setDetail(fnRow, 101, "");
                    setDetail(fnRow, 102, "");
                    
                    if (fnCol == 4)
                        setDetail(fnRow, "nUnitPrce", 0.00);
                    return null;
                }
            default:
                return null;
        }
    }
    
    public boolean addDetail(){return poControl.addDetail();}
    public boolean addDetail(String fsOrderNox){
        addDetail();
        poControl.setDetail(poControl.ItemCount() -1, "sOrderNox", fsOrderNox);
        return true;
    }
    
    public int getDetailCount(){return poControl.ItemCount();}
    
    public void setDetail(int fnRow, int fnCol, Object foData){
        if (pnEditMode != EditMode.UNKNOWN){
            // Don't allow specific fields to assign values
            if(!(fnCol == poDetail.getColumn("sTransNox") ||
                fnCol == poDetail.getColumn("nEntryNox") ||
                fnCol == poDetail.getColumn("dModified"))){
                
                poControl.setDetail(fnRow, fnCol, foData);
//                DetailRetreived(fnCol);
                
                if (fnCol == poDetail.getColumn("nQuantity") ||
                    fnCol == poDetail.getColumn("nUnitPrce") ||
                    fnCol == poDetail.getColumn("nFreightx")){
                }
            }
        }
    }

    public void setDetail(int fnRow, String fsCol, Object foData){       
        setDetail(fnRow, poDetail.getColumn(fsCol), foData);
    }
    
    public Object getDetail(int fnRow, String fsCol){
        return poControl.getDetail(fnRow, fsCol);
    }   
    
    public Object getDetail(int fnRow, int fnCol){
        return poControl.getDetail(fnRow, fnCol);
    }
   
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
        
    private CreditCardTransMulti poControl;
    private UnitCreditCardTrans poDetail = new UnitCreditCardTrans();
    private GRider poGRider;
    private int pnEditMode;
    private String psBranchCd;
    private boolean pbWithParnt = false;
    
    private final String pxeModuleName = "org.rmj.payment.base.CreditCardTransMulti";
}
