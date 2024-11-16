package tamara.valenzuela.verduritassa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AdapterCultivo extends ArrayAdapter<Cultivo> {

    private Context context;
    private List<Cultivo> cultivos;

    public AdapterCultivo(Context context, List<Cultivo> cultivos) {
        super(context,R.layout.item_list,cultivos);
        this.context = context;
        this.cultivos = cultivos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        }

        Cultivo cultivoActual = cultivos.get(position);
        TextView textoItem = convertView.findViewById(R.id.datos);
        textoItem.setText(cultivoActual.getAlias() + ", " + cultivoActual.getFechaCosecha());


        return convertView;
    }

}
