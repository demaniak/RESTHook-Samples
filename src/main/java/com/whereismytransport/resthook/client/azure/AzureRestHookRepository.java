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
    private final String clientBaseUrl;

    public AzureRestHookRepository(String storageConnectionString, String clientBaseUrl){
        this.storageConnectionString=storageConnectionString;
        this.clientBaseUrl=clientBaseUrl;
    }
    private CloudTable getTable() throws URISyntaxException, StorageException {

        CloudTable tableReference = tableClient.getTableReference(tableName);
        tableReference .createIfNotExists();
        return tableReference;
    }
    public boolean initialize(List<String> logs, List<String> restHookBodies){
        try {
            CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
            tableClient=account.createCloudTableClient();
            getTable();
                return true;
        } catch (StorageException | InvalidKeyException | URISyntaxException e) {
            logs.add(e.getStackTrace().toString());
            return false;
        }
    }

    @Override
    public boolean addOrReplaceRestHook(RestHook hook) {
        try {
            RestHookTableEntity entity = new RestHookTableEntity(hook.index,hook.secret, hook.serverUrl, hook.serverRelativeUrl);
            getTable().execute(TableOperation.insertOrReplace(entity));
            return true;
        } catch (StorageException|URISyntaxException  e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<RestHook> getRestHooks() {
        String partitionFilter= TableQuery.generateFilterCondition("PartitionKey", TableQuery.QueryComparisons.EQUAL,RestHookTableEntity.restHookPartitionKey);
        TableQuery<RestHookTableEntity> query=TableQuery.from(RestHookTableEntity.class).where(partitionFilter);

        ArrayList<RestHook> results=new ArrayList<>();
        try {
            for(RestHookTableEntity result:getTable().execute(query)){
                    results.add(new RestHook(result.serverUrl,result.relativeServerUrl, result.secret,result.index,this.clientBaseUrl));
            }
        } catch (URISyntaxException | StorageException e) {
            e.printStackTrace();
        }
        return results;
    }
}
