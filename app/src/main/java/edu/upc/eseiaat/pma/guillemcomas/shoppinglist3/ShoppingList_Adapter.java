package edu.upc.eseiaat.pma.guillemcomas.shoppinglist3;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.List;

/**
 * Created by Guillem CoRa on 23/10/2017.
 */

public class ShoppingList_Adapter extends ArrayAdapter<ShoppingItem> {                                       //Afegir <String>
    public ShoppingList_Adapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View result= convertView;                                                                   //convertView pastilla reciclada

        if(result==null){
            LayoutInflater inflater= LayoutInflater.from(getContext());                             //crea convertView
            result= inflater.inflate(R.layout.shopping_item, null);                                 //assigna 2n layout
        }

        CheckBox checkbox= (CheckBox) result.findViewById(R.id.shoppingItem);                       //busca dins de la pastilla
        ShoppingItem item= getItem(position);
        checkbox.setText(item.getText());
        checkbox.setChecked(item.isChecked());
        return result;
    }
}
