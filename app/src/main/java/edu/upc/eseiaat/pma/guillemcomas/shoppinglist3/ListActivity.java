//NOSE PERQUÈ NO ES VA PUJAR CORRECTAMENT PERÒ ESTÀ FET EL DIA 23/10/2017 A CLASSE!
package edu.upc.eseiaat.pma.guillemcomas.shoppinglist3;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private ArrayList<ShoppingItem> itemList;
    private ShoppingList_Adapter adapter;
    private ListView list;
    private Button btn_add;
    private EditText edit_item;
    private int pos;

    private static final String FILENAME= "shopping_list.txt";
    private static final int MAX_BYTES = 8000;

    private void writeItemList(){
        try {
            FileOutputStream fos= openFileOutput(FILENAME, Context.MODE_PRIVATE);

            for (int i=0; i<itemList.size(); i++){
                ShoppingItem it= itemList.get(i);
                String line= String.format("%s;%b\n", it.getText(), it.isChecked());                //llegirem string;bool+enter
                fos.write(line.getBytes());
            }

            fos.close();

        } catch (FileNotFoundException e) {
            Log.e("cora", "writeItemList: FileNotFoundException");
            Toast.makeText(this, R.string.cannot_write, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("cora", "writeItemList: IOException");
            Toast.makeText(this, R.string.cannot_write, Toast.LENGTH_SHORT).show();
        }
    }

    private void readItemList(){
        itemList= new ArrayList<>();
        try {
            FileInputStream fis= openFileInput(FILENAME);

            byte[] buffer= new byte[MAX_BYTES];
            int nread= fis.read(buffer);

            if (nread>0) {
                String content = new String(buffer, 0, nread);

                String[] lines = content.split("\n");                                               //tractem el que llegim
                for (String line : lines) {                                                         //hem separat per enters
                    String[] parts = line.split(";");                                               //separem per punts i coma
                    itemList.add(new ShoppingItem(parts[0], parts[1].equals("true")));
                }
            }
            fis.close();

        } catch (FileNotFoundException e) {
            Log.i("cora", "readItemList(): FileNotFoundException");
        } catch (IOException e) {
            Log.e("cora", "readItemList: IOException");
            Toast.makeText(this, R.string.cannot_read, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        writeItemList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        list = (ListView) findViewById(R.id.list);
        btn_add = (Button) findViewById(R.id.add_btn);
        edit_item = (EditText) findViewById(R.id.edit_iten);

        readItemList();

        adapter = new ShoppingList_Adapter(this, R.layout.shopping_item, itemList);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        edit_item.setOnEditorActionListener(new TextView.OnEditorActionListener() {                 //metode per enviar desde teclat
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                addItem();
                return true;
            }
        });

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                itemList.get(pos).toggleChecked();
                adapter.notifyDataSetChanged();
                /*ShoppingItem item= itemList.get(pos);
                boolean checked= item.isChecked();
                itemList.get(pos).setChecked();*/
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> list, View item, int pos, long id) {
                maybeRemoveItem(pos);
                return true;
            }
        });
    }

    private void maybeRemoveItem(final int pos) {
        this.pos = pos;
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        String remove_message = getResources().getString(R.string.removemsg);
        builder.setMessage(remove_message + itemList.get(pos).getText() + "?");

        builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                itemList.remove(pos);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private void addItem() {
        String item_txt = edit_item.getText().toString();
        if (!item_txt.isEmpty()) {                                                                    //equivalent a .equals("")
            itemList.add(new ShoppingItem(item_txt));
            adapter.notifyDataSetChanged();
            edit_item.setText("");
        }
        list.smoothScrollToPosition(itemList.size()-1);                                             //size()-1 és l'últim element
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {                                                 //creem menu al layout
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.options, menu);                                                     //carreguem el nostre menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.clear_chk:
                clearChecked();
                return true;
            case R.id.clearAll:
                clearAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearAll() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        builder.setMessage(R.string.confirm_clearAll);
        builder.setPositiveButton(R.string.clear_all, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                itemList.clear();
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private void clearChecked() {
        int i=0;
        while (i<itemList.size()){                                                                  //millor que for per no saltarnos items al borrar
            if (itemList.get(i).isChecked()) itemList.remove(i);
            else i++;
        }
        adapter.notifyDataSetChanged();
    }
}