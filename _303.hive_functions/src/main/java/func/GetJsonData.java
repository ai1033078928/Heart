package func;

import bean.User;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import utils.BeanUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GetJsonData extends GenericUDTF {

    private ArrayList<String> outList = new ArrayList<>();

    @Override
    public StructObjectInspector initialize(StructObjectInspector argOIs) throws UDFArgumentException {

        //1.定义输出数据的列名和类型
        List<String> fieldNames = new ArrayList<>();
        List<ObjectInspector> fieldOIs = new ArrayList<>();


        //2.添加输出数据的列名和类型
        fieldNames.add("value");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);

        return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);
    }

    @Override
    public void process(Object[] objects) throws HiveException {
        String str = objects[0].toString();
        List<User> users = JSON.parseArray(str, User.class);

        String keyStr = objects[1].toString();

        for (int i = 0; i < users.size(); i++) {
            outList.clear();
            String propertyValue = null;
            try {
                propertyValue = (String) BeanUtils.getPropertyValue(users.get(i), keyStr);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            outList.add(propertyValue);
            super.forward(outList);
        }

    }

    @Override
    public void close() throws HiveException {

    }
}
