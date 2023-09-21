package com.medium.machadoah.tabelafipe.main;

import com.medium.machadoah.tabelafipe.model.Dados;
import com.medium.machadoah.tabelafipe.model.DadosVeiculo;
import com.medium.machadoah.tabelafipe.model.Modelos;
import com.medium.machadoah.tabelafipe.model.Veiculo;
import com.medium.machadoah.tabelafipe.service.ConsumoApi;
import com.medium.machadoah.tabelafipe.service.ConverteDados;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    Scanner scanner = new Scanner(System.in);

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    public void exibeMenu(){

        var menu = """
                *** OPÇÕES ***
                - Carro
                - Moto
                - Caminhão
                
                (!) -> Digite uma das opções para consultar: 
                """;

        System.out.println(menu);

        var opcao = scanner.nextLine().toLowerCase();

        String endereco;

        if(opcao.contains("car")) {
            endereco = URL_BASE.concat("carros/marcas");
        } else if(opcao.contains("mo")) {
            endereco = URL_BASE.concat("motos/marcas");
        } else {
            endereco = URL_BASE.concat("caminhoes/marcas");
        }

        var json = consumo.obterDados(endereco);
        System.out.println(json);

        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream().sorted(Comparator.comparing(Dados::codigo)).forEach(System.out::println);
        System.out.println("Informe o código da marca para consulta: ");

        var codigoMarca = scanner.nextLine();

        endereco = endereco.concat("/"+codigoMarca+"/modelos");
        json = consumo.obterDados(endereco);
        var modeloLista = conversor.obterDados(json, Modelos.class);

        System.out.println("Modelos desta marca:\n");
        modeloLista.modelos()
                .stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nDigite um techo do nome do carro a ser buscado: ");

        var nomeVeiculo = scanner.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos()
                .stream().filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("Modelos filtrados: ");

        modelosFiltrados.forEach(System.out::println);

        System.out.println("Digite por favor o código do modelo: ");
        var codigoModelo = scanner.nextLine();

        endereco = endereco.concat("/" + codigoModelo + "/anos");
        json = consumo.obterDados(endereco);

        List<DadosVeiculo> anos = conversor.obterLista(json, DadosVeiculo.class);

        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);

            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\nTodos os veiculos filtrados: ");

        veiculos.forEach(System.out::println);
    }
}
