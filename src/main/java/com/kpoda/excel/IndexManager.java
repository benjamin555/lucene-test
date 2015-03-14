package com.kpoda.excel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author xinghl
 *
 */
public class IndexManager{
    private static IndexManager indexManager;
    private static String content="";
    
    private static String INDEX_DIR = "D:\\luceneIndex";
    private static String DATA_DIR = "D:\\luceneData";
    private static Analyzer analyzer = null;
    private static Directory directory = null;
    private static IndexWriter indexWriter = null;
    
    /**
     * 创建索引管理器
     * @return 返回索引管理器对象
     */
    public IndexManager getManager(){
        if(indexManager == null){
            indexManager = new IndexManager();
        }
        return indexManager;
    }
    /**
     * 创建当前文件目录的索引
     * @param path 当前文件目录
     * @return 是否成功
     */
    public static boolean createIndex(String path){
        Date date1 = new Date();
        List<File> fileList = getFileList(path);
        for (File file : fileList) {
//            content = "";
            //获取文件后缀
            String type = file.getName().substring(file.getName().lastIndexOf(".")+1);
            List<String> strings = null;
            if("xls".equalsIgnoreCase(type)||"xlsx".equalsIgnoreCase(type)){
//                content += xls2String(file,type);
                 strings = xls2List(file, type);
                
                
            }
            System.out.println("name :"+file.getName());
            System.out.println("path :"+file.getPath());
            if (strings!=null) {
            	for (int i = 0; i < strings.size(); i++) {
    				String c = strings.get(i);

                	try{
                        analyzer = new StandardAnalyzer(Version.LUCENE_44);
                        directory = FSDirectory.open(new File(INDEX_DIR));
            
                        File indexFile = new File(INDEX_DIR);
                        if (!indexFile.exists()) {
                            indexFile.mkdirs();
                        }
                        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_44, analyzer);
                        indexWriter = new IndexWriter(directory, config);
                        
                        long rowNum = i;
                        String rowContent = c;
                        
                        addDocument(file, rowNum, rowContent);
                        
                        indexWriter.commit();
                        closeWriter();
            
                        
                    }catch(Exception e){
                        e.printStackTrace();
                    }
    			
    			}
			}
            
            
            
            
            
            
        }
        Date date2 = new Date();
        System.out.println("创建索引-----耗时：" + (date2.getTime() - date1.getTime()) + "ms\n");
        return true;
    }
	protected static void addDocument(File file, long rowNum, String rowContent) throws IOException {
		Document document = new Document();
		document.add(new TextField("filename", file.getName(), Store.YES));
		document.add(new TextField("content", rowContent, Store.YES));
		document.add(new TextField("path", file.getPath(), Store.YES));
		document.add(new LongField("rowNum", rowNum, Store.YES));
		indexWriter.addDocument(document);
	}
    
    /**
     * 读取txt文件的内容
     * @param file 想要读取的文件对象
     * @return 返回文件内容
     */
    public static String txt2String(File file){
        String result = "";
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result = result + "\n" +s;
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 读取doc文件内容
     * @param file 想要读取的文件对象
     * @return 返回文件内容
     */
    public static String doc2String(File file){
        String result = "";
//        try{
//            FileInputStream fis = new FileInputStream(file);
//            HWPFDocument doc = new HWPFDocument(fis);
//            Range rang = doc.getRange();
//            result += rang.text();
//            fis.close();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
        return result;
    }
    
    
    /**
     */
    public static List<String> xls2List(File file, String type){
    	List<String> result = new ArrayList<String>();
        FileInputStream fis = null;
        try{
             fis = new FileInputStream(file);   
            
            Workbook rwb = null;
            if ("xls".equalsIgnoreCase(type)) {
            	   rwb = new HSSFWorkbook(fis);
			}else {
				   rwb = new XSSFWorkbook(fis);
			}
          
            int len  =  rwb.getNumberOfSheets();
            for (int i = 0; i < len; i++) {   
                Sheet rs = rwb.getSheetAt(i);
                for (int j = 0; j <=rs.getLastRowNum(); j++) {   
                	Row row = rs.getRow(j); 
                	if (row!=null) {
                		StringBuilder sb = new StringBuilder();   
                		 for(int k=0;k<row.getLastCellNum();k++)  {
                      	   sb.append(row.getCell(k).toString()+"|");   
                         } 
                		result.add(sb.toString());
					}
                  
                }   
            }   
          
        }catch(Exception e){
            e.printStackTrace();
        }finally{
        	if (fis!=null) {
        		 try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}   
			}
        }
        return result;
    }
    
    /**
     * 读取xls文件内容
     * @param file 想要读取的文件对象
     * @param type 
     * @return 返回文件内容
     */
    public static String xls2String(File file, String type){
        String result = "";
        FileInputStream fis = null;
        try{
             fis = new FileInputStream(file);   
            StringBuilder sb = new StringBuilder();   
            Workbook rwb = null;
            if ("xls".equalsIgnoreCase(type)) {
            	   rwb = new HSSFWorkbook(fis);
			}else {
				   rwb = new XSSFWorkbook(fis);
			}
          
            int len  =  rwb.getNumberOfSheets();
            for (int i = 0; i < len; i++) {   
                Sheet rs = rwb.getSheetAt(i);
                for (int j = 0; j <=rs.getLastRowNum(); j++) {   
                	Row row = rs.getRow(j); 
                	if (row!=null) {
                		 for(int k=0;k<row.getLastCellNum();k++)  {
                      	   sb.append(row.getCell(k).toString()+"\n");   
                         } 
					}
                  
                }   
            }   
          
            result += sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
        	if (fis!=null) {
        		 try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}   
			}
        }
        return result;
    }
    /**
     * 查找索引，返回符合条件的文件
     * @param text 查找的字符串
     * @return 符合条件的文件List
     */
    public static void searchIndex(String text){
        Date date1 = new Date();
        try{
            directory = FSDirectory.open(new File(INDEX_DIR));
            analyzer = new StandardAnalyzer(Version.LUCENE_44);
            DirectoryReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);
    
            QueryParser parser = new QueryParser(Version.LUCENE_44, "content", analyzer);
            Query query = parser.parse(text);
            
            ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
        
            for (int i = 0; i < hits.length; i++) {
                Document hitDoc = isearcher.doc(hits[i].doc);
                System.out.println("____________________________");
                System.out.println(hitDoc.get("filename"));
                System.out.println(hitDoc.get("content"));
                System.out.println(hitDoc.get("path"));
                System.out.println("____________________________");
            }
            ireader.close();
            directory.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        Date date2 = new Date();
        System.out.println("查看索引-----耗时：" + (date2.getTime() - date1.getTime()) + "ms\n");
    }
    /**
     * 过滤目录下的文件
     * @param dirPath 想要获取文件的目录
     * @return 返回文件list
     */
    public static List<File> getFileList(String dirPath) {
        File[] files = new File(dirPath).listFiles();
        List<File> fileList = new ArrayList<File>();
        for (File file : files) {
            if (isTxtFile(file.getName())) {
                fileList.add(file);
            }
        }
        return fileList;
    }
    /**
     * 判断是否为目标文件，目前支持txt xls doc格式
     * @param fileName 文件名称
     * @return 如果是文件类型满足过滤条件，返回true；否则返回false
     */
    public static boolean isTxtFile(String fileName) {
        if (fileName.lastIndexOf(".txt") > 0) {
            return true;
        }else if (fileName.lastIndexOf(".xls") > 0) {
            return true;
        }else if (fileName.lastIndexOf(".xlsx") > 0) {
            return true;
        }else if (fileName.lastIndexOf(".doc") > 0) {
            return true;
        }
        return false;
    }
    
    public static void closeWriter() throws Exception {
        if (indexWriter != null) {
            indexWriter.close();
        }
    }
    /**
     * 删除文件目录下的所有文件
     * @param file 要删除的文件目录
     * @return 如果成功，返回true.
     */
    public static boolean deleteDir(File file){
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(int i=0; i<files.length; i++){
                deleteDir(files[i]);
            }
        }
        file.delete();
        return true;
    }
    public static void main(String[] args){
        File fileIndex = new File(INDEX_DIR);
        if(deleteDir(fileIndex)){
            fileIndex.mkdir();
        }else{
            fileIndex.mkdir();
        }
        
        createIndex(DATA_DIR);
        searchIndex("灏众");
    }
}