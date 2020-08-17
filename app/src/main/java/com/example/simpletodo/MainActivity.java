package com.example.simpletodo;

import org.apache.commons.io.FileUtils;

import android.support.annotation.Nullable;
import android.support.v4.util.LogWriter;
import android.support.v7.widget.RecyclerView;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.simpletodo.ItemsAdapter.OnLongClickListener;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static android.os.FileUtils.*;
import static com.example.simpletodo.ItemsAdapter.*;



public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items;

    Button btnADD;
    EditText etitem;
    RecyclerView rvitems;
    ItemsAdapter itemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        btnADD = findViewById( R.id.btnADD );
        etitem = findViewById( R.id.etitem );
        rvitems = findViewById( R.id.rvitems );

        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener = new OnLongClickListener() {
            @Override
            public void onItemLongClick(int position) {

            }

            @Override
            public void onItemLongClicked(int position) {
                // Delete the item from the model
                items.remove( position );
                // Notify the adapter
                itemsAdapter.notifyItemRemoved( position );
                Toast.makeText( getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT ).show();
                saveItems();

            }

        };
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d( "MainActivity", "Single click at position" + position );
                //create the new activity.
                Intent i = new Intent( MainActivity.this, EDitActivity.class );
                //pass the data being edited.
                i.putExtra( KEY_ITEM_TEXT, items.get( position ) );
                i.putExtra( KEY_ITEM_POSITION, position );
                //display the activity.
                startActivityForResult( i, EDIT_TEXT_CODE );

            }
        };
        itemsAdapter = new ItemsAdapter( items, onLongClickListener, onClickListener );
        rvitems.setAdapter( itemsAdapter );
        rvitems.setLayoutManager( new LinearLayoutManager( this ) );

        btnADD.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoItem = etitem.getText().toString();
                //Add item to the model
                items.add( todoItem );
                //Notify the adapter that we have inserted an item.
                itemsAdapter.notifyItemInserted( items.size() - 1 );
                etitem.setText( "" );
                Toast.makeText( getApplicationContext(), "Item was added", Toast.LENGTH_SHORT ).show();
                saveItems();

            }
        } );


    }



    // handle the result of the edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            // Retrieve the updated text value.
            String itemText = data.getStringExtra( KEY_ITEM_TEXT );
            //extract the original position of the edited item from the position key,
            int position = data.getExtras().getInt( KEY_ITEM_POSITION );

            //update the model at the right position with new item text.
            items.set( position, itemText );
            // notify the adapter that something has changed.
            itemsAdapter.notifyItemChanged( position );
            // Persist the changes
            saveItems();
            Toast.makeText( getApplicationContext(), "Item updated successfully!", Toast.LENGTH_SHORT ).show();

       } else {
            Log.w( "MainActivity", "Unknown call to onActivity Result" );

        }

    }

    private File getDataFile()  {
        return new File(getFilesDir(), "data.txt");
    }

    // This function will load items by reading every line of the data file.
    private void loadItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading Items", e);
            items = new ArrayList<>();
        }

    }


    //This function saves items by writing them into the data file.
    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        }  catch (IOException e) {
             Log.e("MainActivity", "Error writing Items", e);
            items = new ArrayList<>();
        }
    }
}

