package com.example.agenda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agenda.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Button iniciar;
    EditText etUser, etPass;
    TextView titulo, aqui, txt;
    ImageView imagen;


    User userSingle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        titulo = binding.textView;
        aqui = binding.aqui;
        txt = binding.textView4;

        imagen = binding.imageView;

        etUser = binding.etUser;
        etPass = binding.etPassword;

        iniciar = binding.button3;
        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(iniciar.getText().toString().equals("Iniciar")) {
                    single(etUser.getText().toString(), etPass.getText().toString());
                } else {
                    if(checkuser(etUser.getText().toString())) {
                        if(!etUser.getText().toString().isEmpty() && !etPass.getText().toString().isEmpty()) {
                            createUser();
                            titulo.setText("Agenda");
                            txt.setText("¿Usuario nuevo?");
                            aqui.setText("Registrate aquí");
                            iniciar.setText("Iniciar");
                            imagen.setImageResource(R.drawable.ic_undraw_events_re_98ue);
                            Toast.makeText(getApplicationContext(), "Usuario registrado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Favor de llenar todos los campos", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        aqui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(aqui.getText().toString().equals("Registrate aquí")){
                    titulo.setText("Registro");
                    txt.setText("");
                    aqui.setText("Iniciar sesión");
                    iniciar.setText("Registar");
                    imagen.setImageResource(R.drawable.ic_undraw_terms_re_6ak4);

                } else {
                    titulo.setText("Agenda");
                    txt.setText("¿Usuario nuevo?");
                    aqui.setText("Registrate aquí");
                    iniciar.setText("Iniciar");
                    imagen.setImageResource(R.drawable.ic_undraw_events_re_98ue);
                }
            }
        });
        //createUser();
    }

    public void registrar(){

    }

    public void irEventos(){
        Intent i = new Intent(this, EventActivity.class);
        i.putExtra("usuario", userSingle);
        startActivity(i);
        finish();
    }

    public void createUser(){
        Sqlite sqLite = new Sqlite(this);
        SQLiteDatabase sqLiteDatabase = sqLite.getWritableDatabase();

        String name = etUser.getText().toString();
        String password = etPass.getText().toString();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("password", password);

        Long result = sqLiteDatabase.insert("users", null, values);
    }

    public void single(String name, String pass){
        Sqlite sqLite = new Sqlite(this);
        SQLiteDatabase sqLiteDatabase = sqLite.getWritableDatabase();

        Cursor cursor = null;
        String [] args = new String[]{name, pass};

        cursor = sqLiteDatabase.rawQuery("SELECT * from users WHERE name = ? AND password = ?" , new String[]{args[0], args[1]});

        if(cursor.moveToFirst()){
            do {
                    userSingle = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
                    toastCorrecto("Se ha iniciado sesión correctamente");
                    irEventos();
            }while(cursor.moveToNext());
        } else {
            toastIncorrecto("Usuario o contraseña incorrectos");
        }
        cursor.close();
    }

    public boolean checkuser(String name){
        boolean flag;
        Sqlite sqLite = new Sqlite(this);
        SQLiteDatabase sqLiteDatabase = sqLite.getWritableDatabase();

        Cursor cursor = null;
        String [] args = new String[]{name};

        cursor = sqLiteDatabase.rawQuery("SELECT * from users WHERE name = ? " , new String[]{args[0]});

        if(cursor.moveToFirst()){
            do {
                userSingle = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
                toastIncorrecto("Nombre de usuario ocupado");
                flag = false;
            }while(cursor.moveToNext());
        } else {
            flag = true;
        }
        cursor.close();
        return flag;
    }

    public void toastCorrecto(String message){
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.custom_toas_ok, (ViewGroup) findViewById(R.id.llcustom_ok));

        TextView textView = view.findViewById(R.id.txtMessage);
        textView.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

    public void toastIncorrecto(String message){
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.custom_toast_fail, (ViewGroup) findViewById(R.id.llcustom_fail));

        TextView textView = view.findViewById(R.id.txtMessageFail);
        textView.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }
}