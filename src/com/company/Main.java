package com.company;

import java.util.*;
import java.io.*;
import java.lang.Math;

public class Main {

    //our array lists which hold laptop elements and elements read from the txt
    public static ArrayList<String> file_elements = new ArrayList<>();
    public static ArrayList<Laptop> laptop_array = new ArrayList<>();
    public static boolean[] visited;
    public static int[] levels;

    public static void main(String[] args) throws IOException{

        Main main = new Main();
        int num_laptops;

        //calling argument
        File file = new File(args[0]);
        Scanner scan = new Scanner(file);

        //reading text file
        main.read_file(args);

        //number of laptops declared in first line of the txt
        num_laptops = Integer.parseInt(file_elements.get(0));

        //creating laptop objects and storing them into an arraylist
        for(int i = 1; i < file_elements.size(); i+=3){
            laptop_array.add(new Laptop(Double.parseDouble(file_elements.get(i)),
                    Double.parseDouble(file_elements.get(i+1)),
                    Double.parseDouble(file_elements.get(i+2))));
        }


        //creating an instance of our graph representation
        //which consists vertices depending on the number of laptops
        Graph graph = new Graph(num_laptops);
        visited = new boolean[graph.V];
        levels = new int[graph.V];

        //initializing visited as full of false
        Arrays.fill(visited, Boolean.FALSE);
        Arrays.fill(levels, 0);

        //if we checked, for example range between 1 and 2, we don't need to check again
        //so second loop is started from i to avoid this
        for(int i = 0; i < num_laptops; i++)
            for(int j = i; j < num_laptops; j++)
                //if laptops at i and j indexes in the same range
                //there needs to be an edge between them
                if(main.hop_distance(i,j) == 1) graph.add_edge(i,j);

        //initializing BFS traverse
        main.BFS_traverse(graph.adj_list);

        //displaying levels after having the BFS algorithm done
        for(int i = 0; i < levels.length; i++)
            System.out.println(levels[i]);
    }

    //returns 1 if any unvisited neighbour exists corresponding to the index
    // else 0
    public int neighbour_exist(List<List<Integer>> adj_list, int index){

        for(int e: adj_list.get(index))
            if(visited[e] == Boolean.FALSE) return 1;

        return 0;
    }

    public void BFS_traverse(List<List<Integer>> adj_list){

        int level = 0; //initial level
        Queue<Integer> queue = new LinkedList<>(); //queue to be used in BFS algorithm

        levels[0] = level;
        //adding our initial one into queue and continuing on
        queue.add(0);

        //while there is still node to be visited
        while(!queue.isEmpty()){
            //holding head of the queue
            int head = queue.peek();

            //marking it as visited
            visited[head] = Boolean.TRUE;
            //if any unvisited neighbour exists corresponding
            // to current head traverse them as well
            if(neighbour_exist(adj_list, head) == 1)
                if(levels[head] != 0 && queue.contains(head))
                    level = levels[head]+1;
                else
                    level += 1;

            for(int e: adj_list.get(head)){
                //update its level and add to the queue
                if(visited[e] == Boolean.FALSE && levels[e] == 0){
                    queue.add(e);
                    levels[e] = level;
                }

            }
            queue.remove();

        }
    }

    //this function calculates whether 2 laptops in the same range or not depending on mathmetical formulas explained below
    //returns 1 if laptops at declared indexes in the same range, else it will return 0
    public int hop_distance(int index_1, int index_2){

        //position values depending on the laptop number
        double index1_posx = laptop_array.get(index_1).getPos_x();
        double index1_posy = laptop_array.get(index_1).getPos_y();
        double index2_posx = laptop_array.get(index_2).getPos_x();
        double index2_posy = laptop_array.get(index_2).getPos_y();

        //range value depending on the laptop number
        double index1_r = laptop_array.get(index_1).getRange();
        double index2_r = laptop_array.get(index_2).getRange();

        double distance = calculate_distance(index1_posx, index1_posy,
                                            index2_posx, index2_posy);

        //Seperate circles: d > r1+r2
        if(distance > index1_r+index2_r) return 0;

        //Outer tangency between circles: d = r1+r2
        else if(distance == index1_r+index2_r) return 1;

        //Intersecting circles: |r1-r2| < d < |r1+r2|
        else if((Math.abs(index1_r-index2_r) < distance) &&
                distance < Math.abs(index1_r+index2_r)) return 1;

        //Containing within each other: d < |r1-r2|
        else if(distance < Math.abs(index1_r-index2_r)) return 1;

        return 0;
    }

    //calculates and returns the distance between 2 laptop depending on the formula:
    //( |x1-x2|^2 + |y1-y2|^2 )^(-1/2)
    public double calculate_distance(double x_1, double y_1, double x_2, double y_2){
        return Math.sqrt( Math.pow(Math.abs(x_2-x_1), 2) + Math.pow(Math.abs(y_2-y_1), 2) );
    }

    public void read_file(String[] args){

        BufferedReader reader;
        try{
            reader = new BufferedReader(new FileReader(args[0]));
            String line = reader.readLine();
            while(line != null){
                //skipping lines containing explanation
                if(!line.contains("#")){
                    //splitting lines by spaces
                    String[] split = line.split("\\s+");
                    //storing each of them in an arraylist
                    for(String s : split)
                        file_elements.add(s);
                }
                line = reader.readLine();
            }
            reader.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
