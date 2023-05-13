package jacques.raul.uv.photoaday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Comparator;

public class ListaFotosAdapter extends RecyclerView.Adapter<ListaFotosAdapter.MyViewHolder> {
    private final ListaFotosInterface lfi;

    Context context;
    ArrayList<FotoModel> fotoModels;

    public ListaFotosAdapter(ListaFotosInterface lfi, Context context, ArrayList<FotoModel> fotoModels){
        this.lfi = lfi;
        this.context = context;
        this.fotoModels = fotoModels;
    }

    public void updateData(ArrayList<FotoModel> newListaFotos) {
        this.fotoModels = newListaFotos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListaFotosAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Aquí se crea la vista y los elementos de una fila
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_foto, parent, false);

        return new ListaFotosAdapter.MyViewHolder(view, lfi);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaFotosAdapter.MyViewHolder holder, int position) {
        // Asigna los valores a las vistas y lo hace por posición.
        holder.fechaTextView.setText(fotoModels.get(position).getFechaFoto());
        holder.ubicacionTextView.setText(fotoModels.get(position).getUbicacion());

        Glide.with(context)
                .load(fotoModels.get(position).getFoto())
                .circleCrop()
                .into(holder.fotoView);
    }

    @Override
    public int getItemCount() {
        return this.fotoModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView fotoView;
        TextView fechaTextView, ubicacionTextView;

        //Asigna los textos o cosas del layout en variables para poder asignarlas
        public MyViewHolder(@NonNull View itemView, ListaFotosInterface lfi) {
            super(itemView);

            this.fotoView = itemView.findViewById(R.id.imageViewFoto);
            this.fechaTextView = itemView.findViewById(R.id.fechaFoto);
            this.ubicacionTextView = itemView.findViewById(R.id.ubicacionFoto);

            itemView.setOnClickListener(view -> {
                if(lfi != null){
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION){
                        lfi.onItemClick(pos);
                    }
                }
            });
        }
    }

    public void sortBy(Comparator<FotoModel> comparator) {
        fotoModels.sort(comparator);
    }

}
