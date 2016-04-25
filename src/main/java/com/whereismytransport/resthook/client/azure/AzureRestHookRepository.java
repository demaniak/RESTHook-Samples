package com.whereismytransport.resthook.client.azure;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick Cuthbert on 25/04/2016.
 */
public class AzureRestHookRepository {

    protected CloudTableClient tableClient;
    protected CloudTable table;

    private final static String tableName = "JavaTestClientTable";
    private List<String> logs;

    public AzureRestHookRepository(ArrayList<String> logs){
        this.logs=logs;
    }

    public boolean startUp(){
        try {
            CloudStorageAccount account = CloudStorageAccount.parse(ConfigStrings.azureStorageConnectionString);
            tableClient=account.createCloudTableClient();
            table=tableClient.getTableReference(tableName);
            table.createIfNotExists();
            return true;
        } catch (StorageException | InvalidKeyException | URISyntaxException e) {
            logs.add(e.getStackTrace().toString());
            return false;
        }
    }
}
