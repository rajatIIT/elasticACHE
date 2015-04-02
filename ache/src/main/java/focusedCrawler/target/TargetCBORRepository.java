package focusedCrawler.target;

import focusedCrawler.util.Target;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLEncoder;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.ElasticsearchException;




import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;




import com.fasterxml.jackson.dataformat.cbor.*;
import com.fasterxml.jackson.databind.ObjectMapper;
/*
*
*/

public class TargetCBORRepository implements TargetRepository {

  private String location;
  private TargetModel targetModel;
  private boolean writeToElastic;		// to be read from the connfig file if found to be true write to elasticsearch
  private int elasticPageCounter;
  private String elasticServerName;
  private Client client;
  private int elasticCounter=0;
  
  public TargetCBORRepository(){
	targetModel = new TargetModel("Kien Pham", "kien.pham@nyu.edu");//This contact information should be read from config file
	writeToElastic = true; 	// TODO: remove this and read from config
	
	// initialize client
	if (writeToElastic) 
	initializeClient();
  }
  
  private void initializeClient() {
	  Node node = nodeBuilder().clusterName("elasticsearch").node();
		client = node.client();
}

public TargetCBORRepository(String loc){
	targetModel = new TargetModel("Kien Pham", "kien.pham@nyu.edu");//This contact information should be read from config file
	writeToElastic = true; 	// TODO: remove this and read from config
	
	
	  this.location = loc;
	 if (writeToElastic)
	 initializeClient();
	  
  }

  /**
   * The method inserts a page with its respective crawl number.
   */
  public boolean insert(Target target, int counter) {
	boolean contain = false;
    try {
    	URL urlObj = new URL(target.getIdentifier());
		String host = urlObj.getHost();
		String url = target.getIdentifier();

		this.targetModel.setTimestamp();
		this.targetModel.setUrl(url);
		this.targetModel.setContent(target.getSource());
		 
		if(writeToElastic) {
		
			throwToElastic(targetModel);
			
		} else {
		CBORFactory f = new CBORFactory();
    	ObjectMapper mapper = new ObjectMapper(f);
		File dir = new File(location + File.separator + URLEncoder.encode(host));
		if(!dir.exists()){
            dir.mkdir();
        }
		File out = new File(dir.toString() + File.separator + URLEncoder.encode(url) + "_" + counter);
		mapper.writeValue(out, this.targetModel);
		}
		
		
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    return contain;
  }
  
  public void throwToElastic(TargetModel inputTargetModel) throws ElasticsearchException, IOException{
		IndexResponse response = client.prepareIndex("rajatache", "pages", elasticCounter + "")
		        .setSource(inputTargetModel.getJSON())
		        .execute()
		        .actionGet();
		elasticCounter++;
	}
  

  public boolean insert(Target target) {
    boolean contain = false;
    try {
     	URL urlObj = new URL(target.getIdentifier());
		String host = urlObj.getHost();
		String url = target.getIdentifier();
		this.targetModel.setTimestamp();
		this.targetModel.setUrl(url);
		this.targetModel.setContent(target.getSource());
		
		
		if(writeToElastic) {
			
			throwToElastic(targetModel);
			
		} else 	{
		CBORFactory f = new CBORFactory();
    	ObjectMapper mapper = new ObjectMapper(f);
		File dir = new File(location + File.separator + URLEncoder.encode(host));
		if(!dir.exists()){
            dir.mkdir();
        
		File out = new File(dir.toString() + File.separator + URLEncoder.encode(url));
		mapper.writeValue(out, this.targetModel);
		
		}
    }
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    return contain;
  }

  public String getLocation(){
	  return location;
  }
  
}
