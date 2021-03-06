import java.util.HashMap;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;
import java.util.List;

public class App {
  public static void main(String[] args) {
    staticFileLocation("/public");
    String layout = "templates/layout.vtl";

    get("/", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/index.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/categories", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("categories", Category.all());
      model.put("template", "templates/categories.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/recipes", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      System.out.println(request.queryParams("searchIngredients"));
      if (request.queryParams("searchIngredients") != null) {
        String query = request.queryParams("searchIngredients");
        List<Recipe> foundRecipes  = Recipe.searchIngredients("%" + query + "%");
        model.put("foundRecipes", foundRecipes);
        System.out.println("hello");
      }
      List<Recipe> recipes = Recipe.all();
      model.put("recipeRatings", Recipe.allRated());
      model.put("recipes", recipes);
      model.put("template", "templates/recipes.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/recipes", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      String title = request.queryParams("title");
      String ingredients = request.queryParams("ingredients");
      String instructions = request.queryParams("instructions");
      int rating = Integer.parseInt(request.queryParams("star"));
      System.out.println(rating);
      Recipe newRecipe = new Recipe(title, ingredients, instructions, rating);
      newRecipe.save();
      response.redirect("/recipes");
      return null;
    });


    post("/categories", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      String name = request.queryParams("name");
      Category newCategory = new Category(name);
      newCategory.save();
      response.redirect("/categories");
      return null;
    });

    get("/recipes/:id", (request,response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      Recipe recipe = Recipe.find(Integer.parseInt(request.params("id")));
      model.put("recipe", recipe);
      model.put("allCategories", Category.all());
      model.put("template", "templates/recipe.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/categories/:id", (request,response) ->{
      HashMap<String, Object> model = new HashMap<String, Object>();
      Category category = Category.find(Integer.parseInt(request.params("id")));
      model.put("category", category);
      model.put("allRecipes", Recipe.all());
      model.put("template", "templates/category.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/add_recipes", (request, response) -> {
      int recipeId = Integer.parseInt(request.queryParams("recipe_id"));
      int categoryId = Integer.parseInt(request.queryParams("category_id"));
      Category category = Category.find(categoryId);
      Recipe recipe = Recipe.find(recipeId);
      category.addRecipe(recipe);
      response.redirect("/categories/" + categoryId);
      return null;
    });

    post("/add_categories", (request, response) -> {
      int recipeId = Integer.parseInt(request.queryParams("recipe_id"));
      int categoryId = Integer.parseInt(request.queryParams("category_id"));
      Category category = Category.find(categoryId);
      Recipe recipe = Recipe.find(recipeId);
      recipe.addCategory(category);
      response.redirect("/recipes/" + recipeId);
      return null;
    });
    post("/recipes/:id/edit", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      Recipe recipe = Recipe.find(Integer.parseInt(request.params("id")));
      String title = request.queryParams("title");
      String ingredients = request.queryParams("ingredients");
      String instructions = request.queryParams("instructions");
      int rating = Integer.parseInt(request.queryParams("rating"));
      recipe.update(title, ingredients, instructions, rating);
      String url = String.format("/recipes/%d", recipe.getId());
      response.redirect(url);
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/recipes/:id/delete", (request,response) -> {
      int recipeId = Integer.parseInt(request.params("id"));
      Recipe recipe = Recipe.find(recipeId);
      recipe.delete();
      response.redirect("/recipes");
      return null;
    });

    // post("/recipes/:id/delete", (request, response) -> {
    //   HashMap<String, Object> model = new HashMap<String, Object>();
    //   Recipe recipe = Recipe.find(Integer.parseInt(request.params("id")));
    //   recipe.delete();
    //
    //   response.redirect("/recipes");
    //   return new ModelAndView(model, layout);
    // }, new VelocityTemplateEngine());


    post("/categories/:id/edit", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      Category category = Category.find(Integer.parseInt(request.params("id")));
      String categoryName = request.queryParams("categoryName");
      // Category category = Category.find(category.getCategoryId());
      category.update(categoryName);
      String url = String.format("/categories/%d", category.getId());
      response.redirect(url);
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/categories/:id/delete", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      Category category = Category.find(Integer.parseInt(request.params("id")));
      category.delete();

      response.redirect("/categories");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());


    get("/recipes/:id/edit", (request,response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      Recipe recipe = Recipe.find(Integer.parseInt(request.params("id")));
      model.put("recipe", recipe);
      model.put("template", "templates/recipe-edit.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/recipes/:id", (request,response) -> {
      int recipeId = Integer.parseInt(request.params("id"));
      Recipe recipe = Recipe.find(recipeId);
      String newTitle = request.queryParams("title");
      String newIngredients = request.queryParams("ingredients");
      String newInstructions = request.queryParams("instructions");
      int newRating = Integer.parseInt(request.queryParams("rating"));
      recipe.update(newTitle, newIngredients, newInstructions, newRating);
      response.redirect("/recipes/" + recipeId);
      return null;
    });

  }
}
