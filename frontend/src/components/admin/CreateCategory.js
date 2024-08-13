import { useState } from "react";
import { Form, Formik } from "formik";
import * as Yup from "yup";
import InputAdmin from "./AdminInput";
import api from "../../util/api";

const CreateCategory = ({ setCategories }) => {
    const [name, setName] = useState();
    const validate = Yup.object({
        name: Yup.string()
        .required("Category name is required.")
        .min(3,"Category name must be between 3 and 32 characters.")
        .max(32,"Category name must be between 3 and 32 characters.")
        .matches(/^[aA-zZ]/,"Numbers and special characters are not allowed."),
    });

    const submitHandler = async () => {
        try {
            const { data } = await api.post("/api/admin/category",{ name });
            console.log(data)
            setCategories(data.categories)
        } catch (error ) {
            console.log(error.response.data.message)
        }
    };
    const handleChange = (e) => {
        setName(e.target.value)
    }
    return (
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
                        <InputAdmin
                            type="text"
                            name="name"
                            label="Category"
                            placeholder="Category Name"
                            icon="category"
                            onChange={handleChange}
                        />
                        <button className="mt-4 bg-green-500 py-2 px-4 rounded mt-2 text-white">
                            Add
                        </button>
                    </Form>
                )}
            </Formik>
            
        </div>
    );
};

export default CreateCategory;
