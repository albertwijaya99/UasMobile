package id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Recyclerview extends RecyclerView.Adapter<Adapter_Recyclerview.MyViewHolder> {
    Context context;
    ArrayList<User> user;
    public Adapter_Recyclerview(Context c , ArrayList<User> user)
    {
        context = c;
        this.user = user;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview,parent,false));
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final User userData = user.get(position);
        if(!user.get(position).getUri().isEmpty()){
            Glide.with(context).load(Uri.parse(user.get(position).getUri())).into(holder.imageProfile);
        }else{
            Glide.with(context).load(R.drawable.a).into(holder.imageProfile);
        }
        holder.tvName.setText(user.get(position).getName());
        holder.tvPosition.setText(user.get(position).getPosition());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), UserDetailActivity.class);
                intent.putExtra("user",user.get(position));
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.user.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvName, tvPosition;
        CircleImageView imageProfile;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvPosition = (TextView) itemView.findViewById(R.id.tvPosition);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
