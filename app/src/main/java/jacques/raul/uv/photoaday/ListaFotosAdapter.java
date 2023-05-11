package jacques.raul.uv.photoaday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListaFotosAdapter extends RecyclerView.Adapter<ListaFotosAdapter.MyViewHolder> {

    Context context;
    ArrayList<FotoModel> fotoModels;

    public ListaFotosAdapter(Context context, ArrayList<FotoModel> fotoModels){
        this.context = context;
        this.fotoModels = fotoModels;
    }

    @NonNull
    @Override
    public ListaFotosAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Aquí se crea la vista y los elementtos de una fila
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_foto, parent, false);

        return new ListaFotosAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaFotosAdapter.MyViewHolder holder, int position) {
        //Asigna los valores a las vistas y lo hace por posicion.
        holder.fechaTextView.setText(fotoModels.get(position).getFechaFoto().toString());
        holder.ubicacionTextView.setText(fotoModels.get(position).getUbicacion());
        //holder.fotoView.setImageResource(fotoModels.get(position).getFoto());
    }

    @Override
    public int getItemCount() {
        return this.fotoModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView fotoView;
        TextView fechaTextView, ubicacionTextView;

        //Asigna los textos o cosas del layout en variables para poder asignarlas
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            this.fotoView = itemView.findViewById(R.id.imageView);
            this.fechaTextView = itemView.findViewById(R.id.fechaTextView);
            this.ubicacionTextView = itemView.findViewById(R.id.ubicacionTextView);
        }
    }
}
