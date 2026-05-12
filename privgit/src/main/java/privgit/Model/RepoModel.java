package privgit.Model;


public class RepoModel{
    public String Owner_Name;
    public String Repo_Name;

    private Integer Owner_ID;
    private Integer Repo_ID;

    public RepoModel(String Owner_Name, Integer Owner_ID, String Repo_name, Integer Repo_Id){
        this.Owner_ID = Owner_ID;
        this.Owner_Name = Owner_Name;
        this.Repo_Name = Repo_name;
        this.Repo_ID = Repo_Id;
    }

}