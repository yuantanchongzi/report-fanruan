package com.fr.data;

/**
 * @Author: Francis
 * @Description:
 * @TIME: Created on 2018/12/18
 * @Modified by:
 */
public class BaseData extends AbstractTableData {
    // 定义程序数据集的列名与数据保存位置
    private String[] columnNames;
    private Object[][] rowData;
    // 实现构建函数，在构建函数中准备数据
    public BaseData() {
        String[] columnNames = { "Name", "Score" };
        Object[][] datas = { { "Alex", new Integer(15) },
                { "Helly", new Integer(22) }, { "Bobby", new Integer(99) } };
        this.columnNames = columnNames;
        this.rowData = datas;
    }
    // 实现ArrayTableData的其他四个方法，因为AbstractTableData已经实现了hasRow方法
    public int getColumnCount() {
        return columnNames.length;
    }
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }
    public int getRowCount() {
        return rowData.length;
    }
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rowData[rowIndex][columnIndex];
    }

}
