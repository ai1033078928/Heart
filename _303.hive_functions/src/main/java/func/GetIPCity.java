package func;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.lionsoul.ip2region.xdb.Searcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GetIPCity extends GenericUDF {

    private static Searcher searcher;

    static {
        // 1、从 dbPath 加载整个 xdb 到内存。
        InputStream resourceAsStream = GetIPCity.class.getClassLoader().getResourceAsStream("ip2region.xdb");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        byte[] cBuff;
        try {
            while (((nRead = resourceAsStream.read(data, 0, data.length)) != -1)) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();

            cBuff = buffer.toByteArray();

            buffer.close();
            resourceAsStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (buffer != null) buffer.close();
                if (resourceAsStream != null) resourceAsStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 2、使用上述的 cBuff 创建一个完全基于内存的查询对象。
        try {
            searcher = Searcher.newWithBuffer(cBuff);
        } catch (Exception e) {
            System.out.printf("failed to create content cached searcher: %s\n", e);
        }
    }

    @Override
    public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {

        // 返回变量输出类型
        if (arguments.length !=1) {
            throw new UDFArgumentLengthException("please give me only one arg");
        }
        if (!arguments[0].getCategory().equals(ObjectInspector.Category.PRIMITIVE)) {
            throw new UDFArgumentTypeException(1, "i need primitive type arg");
        }
        /*return ObjectInspectorFactory.getStandardMapObjectInspector(
                PrimitiveObjectInspectorFactory.writableStringObjectInspector,
                PrimitiveObjectInspectorFactory.writableStringObjectInspector);*/
        return PrimitiveObjectInspectorFactory.javaStringObjectInspector;
    }

    @Override
    public Object evaluate(DeferredObject[] deferredObjects) throws HiveException {

        String ipString = deferredObjects[0].get().toString();
        String ipCity;
        try {
             ipCity = searcher.search(ipString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ipCity;
    }

    @Override
    public String getDisplayString(String[] strings) {
        return null;
    }

    @Override


    
    public void close() throws IOException {
        searcher.close();
        super.close();
    }
}
