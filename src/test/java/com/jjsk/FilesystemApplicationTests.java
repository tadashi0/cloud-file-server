package com.jjsk;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FilesystemApplicationTests {

	Log log = LogFactory.getLog(FilesystemApplicationTests.class);

	@Value("${spring.data.mongodb.database}")
	String database;

	@Resource
	private MongoClient mongoClient;

	@Resource
	private GridFSBucket gridFsBucket;

	@Resource
	GridFsTemplate gridFsTemplate;

	@Test
	public void findOne() throws Exception {
		// 获取文件ID
		String objectId = "60a2595236147e43e622330c";
		// 创建一个容器，传入一个`MongoDatabase`类实例db
		GridFSBucket bucket = GridFSBuckets.create(mongoClient.getDatabase(database));
		// 获取内容
		GridFSFindIterable gridFSFindIterable = bucket.find(Filters.eq("_id", new ObjectId(objectId)));
		GridFSFile gridFSFile = gridFSFindIterable.first();
		System.out.println("fileInfo: " + gridFSFile);

	}

	public int checkFileMd5(String md5){
		GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("md5").is(md5)).with(Sort.by(Sort.Order.desc("uploadDate"))));
		System.out.println(gridFSFile);
		MongoCollection<Document> chunksCollection = getChunksCollection(mongoClient.getDatabase(database), gridFsBucket.getBucketName());
		FindIterable<Document> findIterable = chunksCollection
				.find(Filters.eq("files_id", gridFSFile.getId()))
				.sort(Filters.eq("n", -1))
				.limit(1);
		int chunkIndex = findIterable.first().getInteger("n", 0);
		int multiply = chunkIndex * 255;
		return Integer.valueOf(Math.abs(multiply / 10485760));
	}


	@Test
	public void test01() {
		MongoCollection<GridFSFile> filesCollection = getFilesCollection(mongoClient.getDatabase(database), gridFsBucket.getBucketName());
		GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is("607e7193f7469561976fdeda")));
		MongoCollection<Document> chunksCollection = getChunksCollection(mongoClient.getDatabase(database), gridFsBucket.getBucketName());
		FindIterable<Document> findIterable = chunksCollection.find(new Document().append("files_id", gridFSFile.getObjectId()));
		MongoCursor<Document> cursor1 = findIterable.iterator();
		while (cursor1.hasNext()){
			System.out.println(cursor1.next());
		}
	}

	private static MongoCollection<GridFSFile> getFilesCollection(final MongoDatabase database, final String bucketName) {
		return database.getCollection(bucketName + ".files", GridFSFile.class).withCodecRegistry(
				fromRegistries(database.getCodecRegistry(), MongoClientSettings.getDefaultCodecRegistry())
		);
	}

	private static MongoCollection<Document> getChunksCollection(final MongoDatabase database, final String bucketName) {
		return database.getCollection(bucketName + ".chunks").withCodecRegistry(MongoClientSettings.getDefaultCodecRegistry());
	}
}
