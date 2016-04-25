package com.whereismytransport.resthook.client.azure;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.ResultContinuation;
import com.microsoft.azure.storage.ResultSegment;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;
import com.whereismytransport.resthook.client.RestHook;
import com.whereismytransport.resthook.client.RestHookRepository;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick Cuthbert on 25/04/2016.
 */
public class AzureRestHookRepository implements RestHookRepository {

    protected CloudTableClient tableClient;
    protected CloudTable table;

    private final static String tableName = "JavaTestClientTable";
    private final String storageConnectionString;
    private List<String> logs;
    private List<String> restHookBodies;

    public AzureRestHookRepository(String storageConnectionString){
        this.storageConnectionString=storageConnectionString;
    }

    public boolean initialize(List<String> logs, List<String> restHookBodies){
        this.logs=logs;
        this.restHookBodies=restHookBodies;
        try {
            CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
            tableClient=account.createCloudTableClient();
            table=tableClient.getTableReference(tableName);
            table.createIfNotExists();
            return true;
        } catch (StorageException | InvalidKeyException | URISyntaxException e) {
            logs.add(e.getStackTrace().toString());
            return false;
        }
    }

    @Override
    public boolean addRestHook(String relativeCallbackUrl, String secret, String serverUrl, String relativeServerUrl) {
        try {
            RestHookTableEntity entity = new RestHookTableEntity(relativeCallbackUrl, secret, serverUrl, relativeServerUrl);
            table.execute(TableOperation.insertOrReplace(entity));
            return true;
        } catch (StorageException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<RestHook> getRestHooks() {
        String partitionFilter= TableQuery.generateFilterCondition("PartitionKey", TableQuery.QueryComparisons.EQUAL,RestHookTableEntity.restHookPartitionKey);
        TableQuery<RestHookTableEntity> query=TableQuery.from(RestHookTableEntity.class).where(partitionFilter);
        ResultContinuation continuationToken = new ResultContinuation();
        ArrayList<RestHook> results=new ArrayList<>();
        for(RestHookTableEntity result:table.execute(query)){
                results.add(new RestHook(result.serverUrl,result.relativeServerUrl, logs,restHookBodies,result.secret));
        }
        return results;
    }
}
