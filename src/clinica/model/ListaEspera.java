package clinica.model;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Queue;

public class ListaEspera {
    Medico medico;
    LocalDate data;
    Queue<Paciente> fila;

    public ListaEspera (Medico medico, LocalDate data){
        this.medico = medico;
        this.data = data;
        fila = new LinkedList<>();
    }

    public void entrarNaFila (Paciente paciente){
        fila.add(paciente);
    }

    
    public Paciente promoverProximo (){
        return fila.poll();
    }

    public boolean estaVazia(){
        return fila.isEmpty();
    }
    public Medico getMedico() {
        return medico;
    }

    public LocalDate getData() {
        return data;
    }

    public Queue<Paciente> getFila() {
        return fila;
    }
    
}
