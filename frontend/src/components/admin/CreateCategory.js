import { useEffect, useState } from "react";
import { Form, Formik } from "formik";
import * as Yup from "yup";
import AdminInput from "./AdminInput";
import api from "../../util/api";
import SelectInput from "./SelectInput";
import slugify from "slugify";


export const loadCategories = async(setCategories) => {
    try {
        const response = await api.get("/admin/categories");

       // subCategories(response.categories);
      //console.log(JSON.stringify(response.data));

      let categoryNames = [] ;
      
      
      for(const [i, value] of response.data.entries()) 
        categoryNames.push({"id" : value.categoryId, "name" : value.categoryName});
      console.log(categoryNames);
      setCategories(categoryNames);     

       return response.data;

       } catch(error) {
           //console.log(error.response.data.message);
       }  
}

const CreateCategory = () => {
    const [name, setName] = useState();
    const [subcategoryName,  setSubcategoryName] = useState();

     
    const [parent, setParent] = useState("");
    const [isLoaded, setIsLoaded] = useState(false);

    const [categories, setCategories ] = useState([]);


    useEffect( () => {
        
        //if (isLoaded) {
        loadCategories(setCategories);        
        //}
        console.log("UseEffect");
        
        //setIsLoaded(true);        
        
      }, [isLoaded]);

    const validate = Yup.object({
        name: Yup.string()
        .required("Category name is required.")
        .min(3,"Category name must be between 3 and 32 characters.")
        .max(32,"Category name must be between 3 and 32 characters.")
        .matches(/^[aA-zZ]/,"Numbers and special characters are not allowed."),
    });

    const validate2 = Yup.object({
        subcategoryName: Yup.string()
            .required("Sub Category name is required.")
            .min(3, "Sub Category name must be between 3 and 32 characters.")
            .max(32, "Sub Category name must be between 3 and 32 characters.")
            .matches(
                /^[aA-zZ]/,
                "Numbers and special characters are not allowed."
            ),
        parent: Yup.string().required("Please choose a parent category"),
    });

    const submitHandler = async () => {
        try {
            
            const slug = slugify(name);
            const reqData = {
               name, slug
            };            
            const { response } = await api.post("/admin/category", reqData);

            let categoryNames = [] ;     

            console.log(JSON.stringify(response.data));
            
            categoryNames.push({"id" : response.data.categoryId, "name" : response.data.categoryName});
              console.log(categoryNames);
              setCategories(categories ||categoryNames);     
            
            //setName("")
        } catch (error ) {
            //console.log(error.response.data.message)
        }
    };
    const handleCategoryChange = (e) => {
        setName(e.target.value)
    }

    const handleSubCategoryChange = (e) => {
        setSubcategoryName(e.target.value)
    }

    
    const submitSubCategoryHandler = async () => {
        try {
            console.log(subcategoryName);
            const slug = slugify(subcategoryName);
            const { data } = await api.post("/admin/subcategory", {
                subcategoryName,
                parent,
                slug
            });

            //setSubCategories(data.subCategory);
            setSubcategoryName("");
            setParent("");
        } catch (error) {
            console.log(error.response.data.message);
        }
    };


    return (
        <>
        <div className="mt-4">
            <div className="flex p-2 border-b pb-1 font-semibold">
                Create a Category
            </div>
            <Formik
                enableReinitialize
                initialValues={{ name }}
                validationSchema={validate}
                onSubmit={() => submitHandler()}
            >
                {(form) => (
                    <Form className="flex-1 flex-col">
                        <AdminInput
                            type="text"
                            name="name"
                            label="Category"
                            placeholder="Category Name"
                            icon="category"
                            onChange={handleCategoryChange}
                        />
                        <button className="mt-4 bg-green-500 py-2 px-4 rounded mt-2 text-white">
                            Add
                        </button>
                    </Form>
                )}
            </Formik>            
        </div>
          <div className="mt-4">
          <div className="flex p-2 border-b pb-1 font-semibold">
              Create a Sub-Category
          </div>
          <Formik
              enableReinitialize
              initialValues={{ subcategoryName, parent }}
              validationSchema={validate2}
              onSubmit={() => submitSubCategoryHandler()}
          >
              {(form) => (
                  <Form className="flex-1 flex-col ">
                      <AdminInput
                          type="text"
                          name="subcategoryName"
                          label="Sub-Category"
                          placeholder="sub Category Name"
                          icon="category"
                          onChange={handleSubCategoryChange}
                      />
                      <SelectInput
                          name="parent"
                          value={parent}
                          data={categories}
                          label="parent category"
                           handleChange={(e) => setParent(e.target.value)}
                      />
                      <button className="mt-4 bg-green-500 py-2 px-4 rounded mt-2 text-white">
                          Add
                      </button>
                  </Form>
              )}
          </Formik>
      </div>
      </>        
    );
};

export default CreateCategory;
