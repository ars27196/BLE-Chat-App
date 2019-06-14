package de.kai_morich.simple_bluetooth_le_terminal.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import de.kai_morich.simple_bluetooth_le_terminal.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    List friendList;
    Context context;
    onMyClickListener mListener;
    String currentUserName;

    public ChatAdapter(Context context, ArrayList listOfFriends, onMyClickListener clickListener) {
        this.context = context;
        friendList = (ArrayList) listOfFriends;
        mListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_new_chat, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        currentUserName = friendList.get(position).toString();
        myViewHolder.title.setText(currentUserName);
        myViewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickListener(position);
            }

        });
        String firstCharacter = "" + currentUserName.charAt(0);

        switch (firstCharacter.trim()) {
            case "a":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.a));
                break;
            case "b":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.b));
                break;
            case "c":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.c));
                break;
            case "d":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.d));
                break;
            case "e":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.e));
                break;
            case "f":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.f));
                break;
            case "g":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.g));
                break;
            case "h":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.h));
                break;
            case "i":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.i));
                break;
            case "j":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.j));
                break;
            case "k":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.k));
                break;
            case "l":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.l));
                break;
            case "m":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.m));
                break;
            case "n":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.n));
                break;
            case "o":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.o));
                break;
            case "p":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.p));
                break;
            case "q":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.q));
                break;
            case "r":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.r));
                break;
            case "s":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.s));
                break;
            case "t":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.t));
                break;
            case "u":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.u));
                break;
            case "v":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.v));
                break;
            case "w":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.w));
                break;
            case "x":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.x));
                break;
            case "y":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.y));
                break;
            case "z":
                glideImageDrawable(myViewHolder.imageView, (R.drawable.z));
                break;
            default:
                glideImageDrawable(myViewHolder.imageView, (R.drawable.blank));
                break;

        }


    }

    public void glideImageDrawable(ImageView imageView, int drawableAddress) {
        //Loading image from url into imageView
        Glide.with(context)
                .load(drawableAddress)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView title, message, date;
        View parentLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.circleImageView);
            title = itemView.findViewById(R.id.tv_title_chat);
            message = itemView.findViewById(R.id.tv_message_chat);
            parentLayout = itemView.findViewById(R.id.parent_chat);
            date = itemView.findViewById(R.id.tv_date_chat);
        }

    }

    interface onMyClickListener {
        void onClickListener(int position);
    }
}
