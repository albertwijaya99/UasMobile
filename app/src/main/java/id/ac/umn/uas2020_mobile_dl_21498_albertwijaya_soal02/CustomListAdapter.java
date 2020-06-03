package id.ac.umn.uas2020_mobile_dl_21498_albertwijaya_soal02;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomListAdapter extends BaseAdapter {
    Context context;
    ArrayList<User> user;

    LayoutInflater inflater;

    public CustomListAdapter(Context c, ArrayList<User> user){
        this.context = c;
        this.user = user;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return user.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.listview, null);

        CircleImageView imageProfile = view.findViewById(R.id.imageProfile);
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvPosition = view.findViewById(R.id.tvPosition);
        LinearLayout linearLayout = view.findViewById(R.id.linearLayout);

        final User userData = user.get(position);
        if(!user.get(position).getUri().isEmpty()){
            Glide.with(context).load(Uri.parse(user.get(position).getUri())).into(imageProfile);
        }else{
            Glide.with(context).load(R.drawable.a).into(imageProfile);
        }

        tvName.setText(user.get(position).getName());
        tvPosition.setText(user.get(position).getPosition());

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), UserDetailActivity.class);
                intent.putExtra("user",user.get(position));
                v.getContext().startActivity(intent);
            }
        });

        return view;
    }
}
