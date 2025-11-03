### 代码实现过程
1. **实体类**
- 首先需要新建实体类，来将excel中每一行数据对应到一个对象中。
- 注解学习
    - @HeadRowHeight(20) 设置行高
    - @ColumnWidth(25) 设置列宽
    - @ExcelProperty(value = "BAL_ID", index = 0) head名称、下标
    - @ExcelIgnore 处理中忽略的列
    - @DateTimeFormat("yyyy-MM-dd HH:mm:ss") 时间格式化
    - @NumberFormat("#.##%") 接收百分比数据
---

2. **监听器**
- 继承AnalysisEventListener<实体>类（重写其中两个方法）
    - invoke 读取一条数据，执行一次该方法。可处理每一行数据
    - doAfterAllAnalysed 读取结束后，执行一次该方法

---

3. **读取**
```java
MyDataReadListener myDataReadListener = new MyDataReadListener();
// 封装工作薄对象
ExcelReaderBuilder workBook = EasyExcel.read(sourceFile, MyData.class, myDataReadListener);
// 封装工作表对象
ExcelReaderSheetBuilder sheet = workBook.sheet();
// 读取
sheet.doRead();
return myDataReadListener.getAllData();
```

---
4. **自定义拦截器**
- 可以用于指定特定单元格样式
- 这里实现了RowWriteHandler接口，重写afterRowDispose方法，将对比结果不同的单元格置上不同的前景色
```java
需要了解的类或接口
WorkbookWriteHandler
SheetWriteHandler
RowWriteHandler
CellWriteHandler
DefaultWriteHandlerLoader
```

---

5. **写入**

```java
EasyExcel.write(tagFile, MyData.class)
        .registerWriteHandler(new 自定义拦截器())
        .sheet()
        .doWrite(数据集(List));
```

---

