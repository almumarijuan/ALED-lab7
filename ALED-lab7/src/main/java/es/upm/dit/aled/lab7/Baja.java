package es.upm.dit.aled.lab7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;



/**
 * Servlet implementation class Baja
 */
@WebServlet("/baja")
public class Baja extends HttpServlet{
	
	@Override
    public void init() {
    	if(getServletContext().getAttribute("repo") == null )
    		getServletContext().setAttribute("repo", new PacienteRepository(getServletContext()));
    	//estoy creando, si no existe el repo donde voy a guardar el pacienterepository para que lo coja qn necesite
    }

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//recupera la info de un fichero
		InputStream file = getServletContext().getResourceAsStream("/baja.html");
		InputStreamReader reader1 = new InputStreamReader(file);
		BufferedReader html = new BufferedReader(reader1);
		
		//Guarda el contenido del fichero en un String
		String pagina = "", linea;
		while((linea = html.readLine()) != null)
			pagina += linea;
		//tenemos que añadir el desplegable con los pacientes
		PacienteRepository pacientes = (PacienteRepository) getServletContext().getAttribute("repo");
		String opciones ="";
		for(Paciente p: pacientes.getPacientes()) {
			String nombre = p.getNombre();
			String apellido = p.getApellido();
			opciones += "<option" + " value='" + p.getDni()+"'>"+nombre + " "+ apellido +"</option> /n";
		}
		PrintWriter out = resp.getWriter();
		resp.setContentType("text/html");
		out.println(pagina.replace("<option></option>", opciones));
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String dni = req.getParameter("paciente");
		PacienteRepository pacientes = (PacienteRepository) getServletContext().getAttribute("repo");
		String mensaje;
		String color;
		if(pacientes.findByDni(dni) != null) {
			//el paciente esta, hay q darle de baja
			pacientes.removePaciente(dni);
			mensaje = "Se ha eliminado correctamente al paciente con DNI: " + dni;
			color = "green";
		}else {
			//el paciente no esta
			mensaje = "El paciente ya ha sido dado de baja";
			color="red";
		}
		//recuperamos el archivo 
		InputStream file = getServletContext().getResourceAsStream("/baja.html");
		InputStreamReader reader1 = new InputStreamReader(file);
		BufferedReader html = new BufferedReader(reader1);
		
		//Guarda el contenido del fichero en un String
		String pagina = "", linea;
		while((linea = html.readLine()) != null)
			pagina += linea;
		
	    //añadimos en h2
		String h2ConMensaje = "<h2 style=\"color:" + color + "\">" + mensaje + "</h2>";
		pagina = pagina.replace("<h2></h2>", h2ConMensaje);
		
		//volvemos a poner las opciones sin el borrado
		String opciones ="";
		for(Paciente p: pacientes.getPacientes()) {
			String nombre = p.getNombre();
			String apellido = p.getApellido();
			opciones += "<option" + " value='" + p.getDni()+"'>"+nombre + " "+ apellido +"</option> /n";
		}
		
		//Devuelve una respuesta al cliente
		PrintWriter out = resp.getWriter();
		resp.setContentType("text/html");
		out.println(pagina.replace("<option></option>", opciones));
		out.close();
	}
	
	

}
