/*
 * Parte do livro "Manual do Indie Gamer", de Cleuton Sampaio, 
 * doravante denominado "O Autor".
 * Este codigo é distribuído sob a licença Apache 2.0, cujo texto pode ser lido em:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Se você for utilizar este código em seus produtos, por favor mencione a autoria
 * original, citando o livro e o nome do Autor.
 * 
 * O código  é disponibilizado "AS IS", e não há nenhum compromisso de suporte ou
 * garantia por parte do Autor.
 * 
 */
package com.obomprogramador.games.xmloader;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.obomprogramador.games.xmloader.GameObject.FORMA_GO;
import com.obomprogramador.games.xmloader.GameObject.TIPO_GO;
import com.obomprogramador.games.xmloader.GameObject.ALINHAMENTO_GO;

import android.content.Context;
import android.util.Log;

public class GameModelLoader {

	public static GameModel loadGameModel (Context ctx, String gameModelName) {
	    GameModel gameModel = new GameModel();
	    
	    try {
		    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    dbFactory.setIgnoringElementContentWhitespace(true);
		    dbFactory.setNamespaceAware(true);
		    dbFactory.setValidating(false);
		    
		    // A validação de esquema XML no Android não funciona! 
		    /*
		     * não é possível instanciar a classe SchemaFactory.
		     */
		    
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(ctx.getAssets().open("modelo/" + gameModelName));
			doc.getDocumentElement().normalize();
			
			NodeList cenas = doc.getElementsByTagName("cena");
			
			for (int x = 0; x < cenas.getLength(); x++) {
				   Node nNode = cenas.item(x);
				   if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				      Element eElement = (Element) nNode;
				      carregarCena(gameModel, eElement);
				   }
			}
	
	    }	    
	    catch (Exception ex) {
	    	Log.d("GAMEMODELLOADER", "Exception loading XML Model: " + ex.getMessage());
	    	gameModel = null;
	    }
	    return gameModel;
	}
	
	private static void carregarCena(GameModel gameModel, Element cenaElement) {
		Cena cena = new Cena();
		cena.setTexturaFundo(getTagValue("texturaFundo", cenaElement));
		cena.setFps(getFloatValue(cenaElement, "fps"));
		cena.setNumero((int) getFloatValue(cenaElement, "numero"));
		cena.setLimitesegundos((int) getFloatValue(cenaElement, "limitesegundos"));
		cena.setParada(getBooleanValue(cenaElement, "parada"));
		cena.setGrupo((int) getFloatValue(cenaElement, "grupo"));
		cena.setMedia(getFloatValue(cenaElement, "media"));
		cena.setRetornoNPC((int) getFloatValue(cenaElement, "retornonpc"));
		gameModel.getCenas().add(cena);
		
		// Frustum
		
		Element frustumElement = getSubElement(cenaElement, "frustum");
		Element frustumCameraEl = getSubElement(frustumElement, "camera");
		ConfigFrustum frustum = new ConfigFrustum();
		cena.setFrustum(frustum);
		frustum.setCamera(new Coordenada(
					getFloatValue(frustumCameraEl, "x"),
					getFloatValue(frustumCameraEl, "y"),
					getFloatValue(frustumCameraEl, "z")
				));
		Element frustumCimaEl = getSubElement(frustumElement, "cima");
		frustum.setCima(
				new Coordenada(
					getFloatValue(frustumCimaEl, "x"),
					getFloatValue(frustumCimaEl, "y"),
					getFloatValue(frustumCimaEl, "z")
				));
		Element frustumDirecaoEl = getSubElement(frustumElement, "direcao");
		frustum.setDirecao(
				new Coordenada(
					getFloatValue(frustumDirecaoEl, "x"),
					getFloatValue(frustumDirecaoEl, "y"),
					getFloatValue(frustumDirecaoEl, "z")
				));
		frustum.setPerto(getFloatValue(frustumElement, "perto"));
		frustum.setLonge(getFloatValue(frustumElement, "longe"));
		
		// Box2D
		
		ConfigBox2D box2d = new ConfigBox2D();
		cena.setBox2d(box2d);
		Element box2dElement = getSubElement(cenaElement, "box2d");
		box2d.setGravidade(getFloatValue(box2dElement, "gravidade"));
		box2d.setSleep(getBooleanValue(box2dElement, "sleep"));
		box2d.setPositionInterations((int)getFloatValue(box2dElement, "positionInterations"));
		box2d.setVelocityInterations((int)getFloatValue(box2dElement, "velocityInterations"));
		box2d.setProporcaoMetroTela(getFloatValue(box2dElement, "proporcaoMetroTela"));
		
		// Carregar Game Objects
		
		Element objetos = getSubElement(cenaElement, "objetos");
		NodeList listaGO = objetos.getElementsByTagName("objeto");
		
		for (int x = 0; x < listaGO.getLength(); x++) {
			   Node nNode = listaGO.item(x);
			   if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			      Element eElement = (Element) nNode;
			      carregarObjeto(cena, eElement);
			   }
		}
		
		// Vamos ver os que estão sobre um objeto cinemático
		
		for (GameObject go :cena.getObjetos()) {
			if (go.getGameObject() == GameObject.GAMEOBJECT_NPC) {
				if (go.getSobre() != 0) {
					GameObject go2 = getObject(go.getSobre(), cena);
					if (go2.getTipo() == TIPO_GO.TIPO_GO_CINEMATICO) {
						if (go2.getEstaoSobre() == null) {
							go2.setEstaoSobre(new ArrayList<GameObject>());
						}
						go2.getEstaoSobre().add(go);
					}
				}
			}
		}
	}
	
	
	protected static GameObject getObject(int id, Cena cena) {
		GameObject g1 = new GameObject();
    	g1.setId(id);
    	int inx = cena.getObjetos().indexOf(g1);
    	g1 = cena.getObjetos().get(inx);
    	return g1;
	}

	private static void carregarObjeto(Cena cena, Element goElement) {
		GameObject go = new GameObject();
		float proporcao = cena.getBox2d().getProporcaoMetroTela();
		cena.getObjetos().add(go);
		go.setId((int)getFloatValue(goElement, "id"));
		go.setAltura(getFloatValue(goElement, "altura"));
		go.setEsticarAltura(getBooleanValue(goElement, "esticarAltura"));
		go.setLargura(getFloatValue(goElement, "largura"));
		go.setEsticarLargura(getBooleanValue(goElement, "esticarLargura"));
		go.setArquivoTextura(getTagValue("arquivoTextura", goElement));
		go.setAtrito(getFloatValue(goElement, "atrito"));
		go.setCoefRetribuicao(getFloatValue(goElement, "coefRetribuicao"));
		go.setDensidade(getFloatValue(goElement, "densidade"));
		go.setForma(FORMA_GO.values()[(int)getFloatValue(goElement, "forma")]);
		go.setTipo(TIPO_GO.values()[(int)getFloatValue(goElement, "tipo")]);
		if (go.getTipo() == TIPO_GO.TIPO_GO_CINEMATICO) {
			go.setVelocidade(getFloatValue(goElement, "velocidade"));
		}
		go.setProibido(getBooleanValue(goElement, "proibido"));
		go.setAlinhamento(ALINHAMENTO_GO.values()[(int)getFloatValue(goElement, "alinhar")]);
		go.setOrdem((int) getFloatValue(goElement,"ordem"));
		go.setSobre((int) getFloatValue(goElement,"sobre"));
		go.setGameObject((int) getFloatValue(goElement,"gameobject"));
		go.setVisivel(getBooleanValue(goElement, "visivel"));
		Element centroEl = getSubElement(goElement, "centro");
		go.setCentro(
				new Coordenada(
						getFloatValue(centroEl, "x"),
						getFloatValue(centroEl, "y"),
						0.0f 

				));
		go.setxOriginal(go.getCentro().getX());
		go.setyOriginal(go.getCentro().getY());
		
	}

	private static Element getSubElement(Element parent, String tag) {
		Element child = null;
		NodeList lista = parent.getElementsByTagName(tag);
		for (int x = 0; x < lista.getLength(); x++) {
			if (lista.item(x).getNodeType() == Node.ELEMENT_NODE) {
				child = (Element) lista.item(x);
				break;
			}
		}
		return child;
	}

	private static float getFloatValue(Element parent, String tag) {
		float valor = Float.parseFloat(
				getTagValue(tag, parent)
				);
		return valor;
	}
	
	private static boolean getBooleanValue(Element parent, String tag) {
		boolean retorno = false;
		String valor = getTagValue(tag, parent);
		
		if (valor != null) {
			Boolean bvalor = Boolean.parseBoolean(valor);
			retorno = bvalor.booleanValue();
		}
		
		return retorno; 
	}
	
	private static String getTagValue(String sTag, Element eElement) {
			String resultado = null;
			try {
				NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
				Node nValue = (Node) nlList.item(0);
		        resultado = (nValue != null) ? nValue.getNodeValue() : null;
			}
			catch (NullPointerException npe) {
				resultado = null;
			}
	        
			return resultado;
	}
}
