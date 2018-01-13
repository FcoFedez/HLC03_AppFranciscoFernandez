package com.example.asus410.hlc03_appfranciscofernandez;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TareaAsincrona tarea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar=(ProgressBar)findViewById(R.id.progresbar);

    }

    public void clickGaleria(View view){
        Intent i = new Intent();
        String url = "https://www.google.es/search?q=galeria+imagenes+competicion+ajedrez&client=firefox-b&dcr=0&tbm=isch&tbo=u&source=univ&sa=X&ved=0ahUKEwiGwOe50KfXAhXPERQKHULnC7MQsAQIJQ&biw=1280&bih=581";
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    /**
     * Método onClick del botón comenzar descarga
     * @param view: botón comenzar descarga
     */
    public void startDescarga(View view) {

        //Si la descarga no se ha iniciado nunca o ha sido cancelada
        if (tarea == null || tarea.isCancelled()) {
            try {
               tarea = new TareaAsincrona();
               tarea.execute(new URL(
                        "http://www.amazon.com/somefiles.pdf"), new URL(
                        "http://www.wrox.com/somefiles.pdf"), new URL(
                        "http://www.google.com/somefiles.pdf"), new URL(
                        "http://www.learn2develop.net/somefiles.pdf"), new URL(
                       "http://www.google.com/somefiles.pdf"), new URL(
                       "http://www.learn2develop.net/somefiles.pdf"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else tostada("Debe esperar a que finalize la descarga");
    }

    /**
     * Método onClick del botón parar descarga: cancela la descarga de archivos
     * @param view: botón comenzar descarga
     */
    public void stopDescarga(View view) {
        if (tarea != null && !tarea.isCancelled()){
            tarea.cancel(true);
        }else tostada("No hay descargas activas");

    }

    public void tostada(String s){
       Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private class TareaAsincrona extends AsyncTask <URL, Integer, Long> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setMax(100); //valor máximo
            progressBar.setProgress(0); //inicialización
        }

        @Override
        protected Long doInBackground(URL... urls) {
            int count = urls.length;
            long totalBytesDownloaded = 0;
            for (int i = 0; i < count; i++) {
                totalBytesDownloaded += DownloadFile(urls[i]);
                // ---calcula el porcentaje descargado  reportando el progreso a onProgressUpdate()
                publishProgress((int) ((((i + 1) / (float) count) * 100)),(i+1));
                if (isCancelled()) break;
            }
            return totalBytesDownloaded;
        }

        /**
         * Método que se e invoca en el hilo de ejecución de la interfaz de usuario y se
         * llama cada vez que se ejecuta el método publishProgress()
         * @param progress valor obtenido en publishProgress
         */
        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            progressBar.setProgress(progress[0]);
            Log.d("Descargando Archivos", String.valueOf(progress[0])
                    + "% descargado");
            Toast.makeText(getBaseContext(),
                    String.valueOf(progress[0]) + "% descargado, Imagen"+progress[1],
                    Toast.LENGTH_LONG).show();
        }

        /**
         * Método que se invoca en el hilo de ejecución de la interfaz de usUario y se llama
         * cuando el método doInBackground() ha terminado de ejecutarse
         * @param result valor recibido de doInBackground()
         */

        @Override
        protected void onPostExecute(Long result) {
            // para que startDescarga pueda iniciarla de nuevo
            tarea = null;

            Toast.makeText(getBaseContext(),
                    "Descargados " + result + " bytes", Toast.LENGTH_LONG)
                    .show();

            progressBar.setProgress(0);
        }

        @Override
        protected void onCancelled(Long result) {
            Toast.makeText(getApplicationContext(), "Tarea cancelada! Descargados "+result+ " bytes",
                    Toast.LENGTH_LONG).show();
            //tarea no iniciada
            tarea = null;
            progressBar.setProgress(0);
        }


        /**
         * Método que simula la descarga de un archivo -- tarea de larga duración
         * @param url : url del archivo a descargar
         * @return. valor devuelto (bytes descargados)
         */

        private int DownloadFile(URL url) {

            try {
                // ---simula el tiempo necesario para descargar el archivo---
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // devuelve el tamaño en bytes simulado de la descarga--
            return 100;
        }


    }
}
