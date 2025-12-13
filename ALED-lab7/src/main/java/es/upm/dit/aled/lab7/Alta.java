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
 * Servlet implementation class Alta
 */
@WebServlet("/alta")
public class Alta extends HttpServlet {

    @Override
    public void init() {
    	if(getServletContext().getAttribute("repo") == null )
    		getServletContext().setAttribute("repo", new PacienteRepository(getServletContext()));
    	//estoy creando, si no existe el repo donde voy a guardar el pacienterepository para que lo coja qn necesite
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Recupera un stream al fichero
		InputStream file = getServletContext().getResourceAsStream("/alta.html");
		InputStreamReader reader1 = new InputStreamReader(file);
		BufferedReader html = new BufferedReader(reader1);
		
		//Guarda el contenido del fichero en un String
		String pagina = "", linea;
		while((linea = html.readLine()) != null)
			pagina += linea;
		
		//Devuelve una respuesta al cliente
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		out.println(pagina);
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//crear un paciente con los datos que da /alta.html
		String nombre = request.getParameter("nombre");
		String apellidos = request.getParameter("apellidos");
		String dni = request.getParameter("dni");
		
		Paciente paciente = new Paciente(nombre, apellidos, dni);
		
		//guardo en un string página el contenido de alta
		InputStream file = getServletContext().getResourceAsStream("/alta.html");
		InputStreamReader reader1 = new InputStreamReader(file);
		BufferedReader html = new BufferedReader(reader1);
		
		//Guarda el contenido del fichero en un String
		String pagina = "", linea;
		while((linea = html.readLine()) != null)
			pagina += linea;
		
		//comprobamos si el paciente está en paceinterepository
		PacienteRepository repo = (PacienteRepository) getServletContext().getAttribute("repo");
		String mensaje;
		if(repo.findByDni(dni) != null) {
			//esta el pacinte
			mensaje = "Este paciente ya existe";
		}else {
			//no está el paciente
			repo.addPaciente(paciente);
			mensaje = "El paciente se ha dado de alta correctamente";
		}
		
		pagina = pagina.replace("<h2></h2>", "<h2>" + mensaje +"</h2>");
		//Devuelve una respuesta al cliente
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		out.println(pagina);
		out.close();
	}
}
