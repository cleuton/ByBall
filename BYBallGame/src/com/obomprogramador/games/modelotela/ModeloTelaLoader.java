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
package com.obomprogramador.games.modelotela;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.obomprogramador.games.xmloader.Coordenada;
import com.obomprogramador.games.xmloader.GameObject.FORMA_GO;
import com.obomprogramador.games.xmloader.GameObject.TIPO_GO;
import com.obomprogramador.games.xmloader.GameObject.ALINHAMENTO_GO;

import android.content.Context;
import android.util.Log;

public class ModeloTelaLoader {
	
	private static float alturaViewPort;
	private static float larguraViewPort;
	private static float diagonalTela;

	public static ModeloTela loadXML (Context ctx, String modeloTelaName, float mAlturaViewPort, float mLarguraViewPort) {
		ModeloTela modeloTela = new ModeloTela();
	    ModeloTelaLoader.alturaViewPort = mAlturaViewPort;
	    ModeloTelaLoader.larguraViewPort = mLarguraViewPort;
	    ModeloTelaLoader.diagonalTela = (float) (Math.pow(ModeloTelaLoader.alturaViewPort, 2) + Math.pow(ModeloTelaLoader.larguraViewPort, 2));
	    ModeloTelaLoader.diagonalTela = (float) Math.sqrt(ModeloTelaLoader.diagonalTela);
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
			Document doc = dBuilder.parse(ctx.getAssets().open("texturas/" + modeloTelaName));
			doc.getDocumentElement().normalize();
			
			NodeList posicoes = doc.getElementsByTagName("posicao");
			
			for (int x = 0; x < posicoes.getLength(); x++) {
				   Node nNode = posicoes.item(x);
				   if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				      Element eElement = (Element) nNode;
				      carregarPosicao(modeloTela, eElement);
				   }
			}
			
			NodeList texturas = doc.getElementsByTagName("textura");
			
			for (int x = 0; x < texturas.getLength(); x++) {
				   Node nNode = texturas.item(x);
				   if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				      Element eElement = (Element) nNode;
				      carregarTextura(modeloTela, eElement);
				   }
			}
	
	    }	    
	    catch (Exception ex) {
	    	Log.d("ModeloTelaLoader", "Exception loading XML Model: " + ex.getMessage());
	    	modeloTela = null;
	    }
	    return modeloTela;
	}
	
	private static void carregarPosicao(ModeloTela modeloTela, Element posicaoElement) {
		
		PosicaoTela pos = new PosicaoTela();
		pos.setId((int) getFloatValue(posicaoElement, "id"));
		pos.setAltura(getFloatValue(posicaoElement, "altura"));
		pos.setLargura(getFloatValue(posicaoElement, "largura"));
		pos.setTopo(getFloatValue(posicaoElement, "topo"));
		pos.setEsquerda(getFloatValue(posicaoElement, "esquerda"));
		pos.setCentraVertical(getBooleanValue(posicaoElement, "centralizarvertical"));
		pos.setCentraHorizontal(getBooleanValue(posicaoElement, "centralizarhorizontal"));
		pos.setCentro(new Coordenada(0.0f,0.0f,0.0f));
		pos.setContainer((int) getFloatValue(posicaoElement, "container"));
		pos.setFracaoEsquerda(getFloatValue(posicaoElement, "fracaoEsquerda"));
		pos.setFracaoTopo(getFloatValue(posicaoElement, "fracaoTopo"));
		try {
			pos.setRepetirHorizontal((int) getFloatValue(posicaoElement, "repetirhorizontal"));
			pos.setRepetirGrupoVertical((int) getFloatValue(posicaoElement, "repetirgrupovertical"));
			pos.setCentraGrupo(getBooleanValue(posicaoElement, "centragrupo"));
			pos.setEspaco(getFloatValue(posicaoElement, "espaco"));
		}
		catch (Exception ex) {
			
		}
		modeloTela.getPosicoes().add(pos);
		
		List<PosicaoTela> grupo = null;
		int lastId = pos.getId();
		
		float alturaContainer = ModeloTelaLoader.alturaViewPort;
		float larguraContainer = ModeloTelaLoader.larguraViewPort;
		float acrescimoTopo = 0.0f;
		float acrescimoEsquerda = 0.0f;
		
		if (pos.getContainer() != 0) {
			PosicaoTela pt = ModeloTelaLoader.getPosicao(modeloTela, pos.getContainer());
			acrescimoTopo = pt.getTopo();
			acrescimoEsquerda = pt.getEsquerda();
			alturaContainer = pt.getAltura();
			larguraContainer = pt.getLargura();
		}
		
		if (pos.getFracaoTopo() > 0) {
			/*
			 * A fração topo é calculada do canto superior para o canto inferior.
			 * 0.20 significa 20% da tela, a partir de cima.
			 * Temos que calcular sobre as dimensões de tela e depois reduzir.
			 * O topo é calculado em percentual da diagonal da tela
			 */
			float topo = (alturaContainer * pos.getFracaoTopo()) + acrescimoTopo;
			if (pos.getContainer() == 0) {
				topo = (topo / ModeloTelaLoader.diagonalTela) * 100;
			}
			pos.setTopo(topo);
		}
		
		if (pos.getFracaoEsquerda() > 0) {
			/*
			 * A fração esquerda é calculada do canto esquerdo para o canto direito.
			 * 0.20 significa 20% da tela, a partir da esquerda.
			 * Temos que calcular sobre as dimensões de tela e depois reduzir.
			 *
			 */
			float esquerda = (larguraContainer * pos.getFracaoEsquerda()) + acrescimoEsquerda;
			if (pos.getContainer() == 0) {
				esquerda = (esquerda / diagonalTela) * 100;
			}
			
			pos.setEsquerda(esquerda);
		}
		
		if (pos.isCentraGrupo()) {
			// é para centralizar o grupo todo na horizontal
			float tamIntervalo = pos.getLargura() * pos.getEspaco();
			float tamTotal = pos.getLargura() 
					+ pos.getRepetirHorizontal() * (tamIntervalo + pos.getLargura());
			if (pos.getContainer() == 0) {
				tamTotal = (tamTotal * ModeloTelaLoader.diagonalTela) / 100;
			}
			float posEsquerda = (larguraContainer - tamTotal) / 2;
			if (pos.getContainer() == 0) {
				posEsquerda = (posEsquerda / diagonalTela) * 100;
			}
			else {
				posEsquerda = pos.getEsquerda() + posEsquerda;
			}
			pos.setEsquerda(posEsquerda);
			
		}
		
		if (pos.getRepetirHorizontal() > 0) {
			grupo = new ArrayList<PosicaoTela>();
			PosicaoTela posg = null;
			try {
				posg = (PosicaoTela) pos.clone();
				posg.setRepetirHorizontal(0);
				posg.setRepetirGrupoVertical(0);
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			grupo.add(posg);
			float tamIntervalo = pos.getLargura() * pos.getEspaco();
			float posEsquerda = pos.getEsquerda() + pos.getLargura() + tamIntervalo;
			for (int x=0; x<(pos.getRepetirHorizontal()); x++) {
				try {
					posg = (PosicaoTela) pos.clone();
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				posg.setId(lastId + 1);
				posg.setEsquerda(posEsquerda);
				posEsquerda += posg.getLargura() + tamIntervalo;
				posg.setRepetirHorizontal(0);
				posg.setRepetirGrupoVertical(0);
				posg.setContainer(0);
				posg.setFracaoEsquerda(0);
				posg.setFracaoTopo(0);
				grupo.add(posg);
				modeloTela.getPosicoes().add(posg);
				lastId++;
			}
		}
		
		if (pos.getRepetirGrupoVertical() > 0) {
			for (int conta=0; conta<pos.getRepetirGrupoVertical();conta++) {
				float interTopo = pos.getAltura() * pos.getEspaco();
				float topo = pos.getTopo() + pos.getAltura() + interTopo;
				float tamIntervalo = pos.getLargura() * pos.getEspaco();
				float posEsquerda = pos.getEsquerda();
				for (PosicaoTela posg : grupo) {
					PosicaoTela posrg = null;
					try {
						posrg = (PosicaoTela) posg.clone();
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					posrg.setId(lastId + 1);
					lastId++;
					posrg.setTopo(topo);
					posrg.setEsquerda(posEsquerda);
					posEsquerda += posrg.getLargura() + tamIntervalo;
					modeloTela.getPosicoes().add(posrg);
				}
				topo += pos.getAltura();
			}
		}
	}
	
	private static void carregarTextura(ModeloTela modeloTela, Element texturaElement) {
	
		Textura textura = new Textura();
		textura.setId((int) getFloatValue(texturaElement, "id"));
		textura.setAscii((int) getFloatValue(texturaElement, "ascii"));
		textura.setImagem(getTagValue("imagem", texturaElement));
		textura.setVisivel(getBooleanValue(texturaElement, "visivel"));
		textura.setClicavel(getBooleanValue(texturaElement, "clicavel"));
		textura.setPosicaoFixa((int) getFloatValue(texturaElement, "posicaoFixa"));
		textura.setTipoAcao((int) getFloatValue(texturaElement, "tipoacao"));
		if (textura.getTipoAcao() == 1) {
			textura.setNovoNivel((int) getFloatValue(texturaElement, "novonivel"));
			textura.setArquivoModelo(getTagValue("arquivomodelo", texturaElement));
			textura.setArquivoTela(getTagValue("arquivotela", texturaElement));
		}
		else if (textura.getTipoAcao() == 2) {
				textura.setExibirTela((int) getFloatValue(texturaElement, "exibirtela"));
		}
		modeloTela.getTexturas().add(textura);
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
		String valor = getTagValue(tag, parent);
		Boolean bvalor = Boolean.parseBoolean(valor);
		return bvalor.booleanValue();
	}
	
	private static String getTagValue(String sTag, Element eElement) {
			String resultado = null;
			NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	        Node nValue = (Node) nlList.item(0);
	        resultado = (nValue != null) ? nValue.getNodeValue() : null;
			return resultado;
	}
	
	private static PosicaoTela getPosicao(ModeloTela modeloTela, int id) {
		PosicaoTela pt = new PosicaoTela();
		pt.setId(id);
		int inx = modeloTela.getPosicoes().indexOf(pt);
		pt = modeloTela.getPosicoes().get(inx);
		return pt;
	}
}
