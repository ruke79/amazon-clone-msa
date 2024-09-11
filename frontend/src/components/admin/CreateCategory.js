import { useEffect, useState } from "react";
import { Form, Formik } from "formik";
import * as Yup from "yup";
import AdminInput from "./AdminInput";
import { getRequest, postRequest } from "../../util/api";
import SelectInput from "./SelectInput";
import slugify from "slugify";
import toast from "react-hot-toast";


export const loadCategories = async(setCategories) => {
    try {
        const response = await getRequest("/admin/categories");
      
            
      setCategories(response.data);            

       } catch(error) {
           toast.error("Faild to load categories");
           
       }  
}

export const loadSubCategories = async(setSubCategories) => {
    try {
        const response = await getRequest("/admin/product/subcategories");
      
      
      
      setSubCategories(response.data);            

       } catch(error) {
            toast.error("Faild to load subcategories");
           
       }  
}

const CreateCategory = () => {
    const [name, setName] = useState();
    const [subcategoryName,  setSubcategoryName] = useState();
    
    const [parent, setParent] = useState("");
    
    const [categories, setCategories ] = useState([]);
    const [subCategories, setSubCategories ] = useState([]);
    const [refresh, setRefresh] = useState(false);
    const [refresh2, setRefresh2] = useState(false);

    useEffect( () => {
                
        loadCategories(setCategories);                
        setRefresh(false);
        
      }, [refresh]);

      useEffect( () => {
                
        loadSubCategories(setSubCategories);                
        setRefresh2(false);
        
      }, [refresh2]);

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
            const { response } = await postRequest("/admin/category", reqData);

             let categoryNames = [] ;     

                        
             categoryNames.push({"id" : response.data.id, "name" : response.data.name});
              
              setCategories(categories || categoryNames );     

            //setName("")
        } catch (error ) {            
            toast.error("Failed to add a category");
        }

        setRefresh(true);
    };
    const handleCategoryChange = (e) => {
        setName(e.target.value)
    }

    const handleSubCategoryChange = (e) => {
        setSubcategoryName(e.target.value)
    }

    
    const submitSubCategoryHandler = async () => {

        
        try {
            
            const slug = slugify(subcategoryName);
            const { response } = await postRequest("/admin/subcategory", {
                subcategoryName,
                parent,
                slug
            });

            let categoryNames = [] ;     

                       
             categoryNames.push({"id" : response.data.id, "name" : response.data.name});

            setSubCategories(subCategories ||categoryNames);
           
                        
            setSubcategoryName("");
            setParent("");

            setRefresh2(true);
        } catch (error) {
            toast.error("Failed to add SubCategories");
            
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
            {categories?.map((cat, i) => (
                    <div key={i}>{cat.name}</div>
                ))}
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
          {subCategories?.map((cat, i) => (
                    <div key={i}>{cat.name}</div>
                ))}
      </div>
      </>        
    );
};

export default CreateCategory;
