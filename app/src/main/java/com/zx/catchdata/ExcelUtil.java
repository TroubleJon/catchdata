package com.zx.catchdata;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * 作者：H7111906 on 2019/7/31 16:32
 */
public class ExcelUtil {

    public static String readXLS(String path) {
        String str = "";
        try {
            Workbook workbook = null;
            workbook = Workbook.getWorkbook(new File(path));
            Sheet sheet = workbook.getSheet(0);
            Cell cell = null;
            int columnCount = sheet.getColumns();
            int rowCount = sheet.getRows();
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < columnCount; j++) {
                    cell = sheet.getCell(j, i);
                    String temp2 = "";
                    if (cell.getType() == CellType.NUMBER) {
                        temp2 = ((NumberCell) cell).getValue() + "";
                    } else if (cell.getType() == CellType.DATE) {
                        temp2 = "" + ((DateCell) cell).getDate();
                    } else {
                        temp2 = "" + cell.getContents();
                    }
                    str = str + "  " + temp2;
                }
                str += "\n";
            }
            workbook.close();
        } catch (Exception e) {
        }
        if (str == null) {
            str = "解析文件出现问题";
        }
        return str;
    }

    /**
     * 导出xls文件
     *
     * @author xiaoheng
     *
     * @param dataList
     *            文件数据源
     * */
    public static int exportXLS(ArrayList<FireTools> dataList, String fileName) {
        WritableWorkbook wwb = null;
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File directory_export = new File(sdCard, fileName);
            // 首先要使用Workbook类的工厂方法创建一个可写入的工作薄(Workbook)对象
            wwb = Workbook.createWorkbook(new File(directory_export + ".xls"));
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        if (wwb != null) {
            // 创建一个可写入的工作表
            // Workbook的createSheet方法有两个参数，第一个是工作表的名称，第二个是工作表在工作薄中的位置
            WritableSheet ws = wwb.createSheet("工作表名称", 0);

            // 下面开始添加单元格
            String[] topic = {"id","明码","生产厂家", "产品档案号", "产品名称", "规格型号", "表单类型", "经销商信息", "销售类别", "到达日期", "流向地区", "部门", "柱位"};
            for (int i = 0; i < topic.length; i++) {
                Label labelC = new Label(i, 0, topic[i]);
                try {
                    // 将生成的单元格添加到工作表中
                    ws.addCell(labelC);
                } catch (RowsExceededException e) {
                    e.printStackTrace();
                    return 0;
                } catch (WriteException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
            FireTools model;
            ArrayList<String> li;
            for (int i = 0; i < dataList.size(); i++) {
                model = dataList.get(i);
                li = new ArrayList<String>();
                li.add(model.getId()+"");
                li.add(model.getCode()+"");
                li.add(model.getProducer()+"");
                li.add(model.getFileNO()+"");
                li.add(model.getName()+"");
                li.add(model.getModel()+"");
                li.add(model.getFormType()+"");
                li.add(model.getDealerInfo()+"");
                li.add(model.getSaleType()+"");
                li.add(model.getArriveDate()+"");
                li.add(model.getSaleArea()+"");
                li.add(model.getLocation()+"");
                li.add(model.getPillarindex()+"");
                int k = 0;
                for (String l : li) {
                    Label labelC = new Label(k, i + 1, l);
                    k++;
                    try {
                        // 将生成的单元格添加到工作表中
                        ws.addCell(labelC);
                    } catch (RowsExceededException e) {
                        e.printStackTrace();
                    } catch (WriteException e) {
                        e.printStackTrace();
                    }
                }
                li = null;
            }
        }
        try {
            // 从内存中写入文件中
            wwb.write();
            // 关闭资源，释放内存
            wwb.close();
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return 0;
    }


}
