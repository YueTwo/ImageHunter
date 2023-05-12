package util;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DBListener {
    //id与置信度类
    public class Pair_Id_confidence {

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public float getConfidence() {
            return confidence;
        }

        public void setConfidence(float confidence) {
            this.confidence = confidence;
        }

        private String path;
        private float confidence;

        //含参的构造方法
        Pair_Id_confidence(String path, float confidence) {
            setPath(path);
            setConfidence(confidence);
        }


    }

    private BinaryLogClient.EventListener eventListener;
    List<Pair_Id_confidence> id_confidence_List;
    private final String Database_name = "124_221_95_230";
    BinaryLogClient client = new BinaryLogClient(
            "124.221.95.230",
            3309,
            "user",
            "123456"
    );


    /**
     * 此函数用于监听检索返回结果的表,将结果（文件本地路径，置信度）对象 放入list中
     *
     * @param tag     1:监听图片结果表  2:监听视频结果表
     * @param text_id 这个是插入的时会返回的文本主键
     */
    public void search_result_listener(int tag, int text_id) {
        //这里根据tag判断要监听的表
        String result_table = null;
        String id_column = null;
        if (tag == 1) {
            result_table = "image_search_result_table";
            id_column = "image_id";
        } else if (tag == 2) {
            result_table = "video_search_result_table";
            id_column = "video_id";
        }
        //将两个字符串写死，不然匿名函数中不能使用
        final String finalResult_table = result_table;
        final String finalId_column = id_column;
        //开启监听

        this.client.registerEventListener(event -> {
            EventData data = event.getData();
            //如果是写入操作（即可能是模型端返回了结果）
            if (data instanceof TableMapEventData) {
                TableMapEventData tableMapEventData = (TableMapEventData) data;
                if (tableMapEventData.getDatabase().equals(Database_name)
                        && tableMapEventData.getTable().equals(finalResult_table)) {
                    //建立数据库操作对象获取到检索的置信结果
                    DBUtils dbUtils = new DBUtils();
                    try {
                        //获取到result (包括视频/图片id和置信度)
                        ResultSet resultSet = dbUtils.search_result(tag, text_id);
                        while (resultSet.next()) {
                            Pair_Id_confidence tmp_pair = new Pair_Id_confidence(resultSet.getString("path"), resultSet.getFloat("confidence"));
                            this.id_confidence_List.add(tmp_pair);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });
    }

    /**
     * 检索结束监听，此方法用在检索文本发出以后，监听模型端的检索结束标志，如果监听到模型端检索结束，开始查询数据库查找检索结果
     *
     * @param text_id
     */

    public void retrieve_end_listener(int text_id) throws IOException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        this.eventListener = event -> {
//            boolean is_table = false;
            EventData data = event.getData();
            if (data instanceof WriteRowsEventData && ((WriteRowsEventData) data).getTableId()==123) {

                List<Serializable[]> rows = ((WriteRowsEventData) data).getRows();
                for (Serializable[] row : rows) {
                    if (Objects.equals(row[1].toString(), Integer.toString(text_id))) {
//                        检测到text_id 断开连接
                        this.client.unregisterEventListener(this.eventListener);
                        // Disconnect the client in a new thread
                        executor.submit(() -> {
                            try {
                                this.client.disconnect();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            executor.shutdown();
                        });

                        return;

                    }
                }
            }
        };
        this.client.registerEventListener(this.eventListener);
        this.client.connect();
    }

    /**
     * 监听 video_table or image_table 的text_generate_text列，如果被写入了，就结束监听
     * image_table :297   video_table :299
     *
     * @param file_id
     * @param tag
     */
    public void text_generation_listener(int file_id, int tag) throws IOException {
        int table_id;
        if (tag == 1) {//image
            table_id = 122;
        } else {//视频
            table_id = 118;
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int finalTable_id = table_id;
        this.eventListener = event -> {
//            boolean is_table = false;
            EventData data = event.getData();
//            System.out.println(((UpdateRowsEventData) data).getTableId());
            String listen_table_name="";
            if(data instanceof TableMapEventData){
                TableMapEventData tableMapEventData=(TableMapEventData) data;
                listen_table_name=((TableMapEventData) data).getTable();
            }
            if (data instanceof UpdateRowsEventData && ((UpdateRowsEventData) data).getTableId() == finalTable_id) {
                List<Map.Entry<Serializable[], Serializable[]>> rows = ((UpdateRowsEventData) data).getRows();
                for (Map.Entry<Serializable[], Serializable[]> row : rows) {
                    Serializable[] keys = row.getKey();
                    Serializable[] values = row.getValue();
//                    System.out.println(keys[0].toString());
//                    System.out.println(values[8].toString());
                    if (keys[0].equals(file_id) && values[7] != null) {
                        // 监听到对应行text列的更新，进行相关操作
                        this.client.unregisterEventListener(this.eventListener);
                        // Disconnect the client in a new thread
                        executor.submit(() -> {
                            try {
                                this.client.disconnect();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            executor.shutdown();
                        });

                        return;
                    }
                }
            }

        };
        this.client.registerEventListener(this.eventListener);
        this.client.connect();

    }
}
