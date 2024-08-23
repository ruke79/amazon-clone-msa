import { useState } from "react";
import { Form, Formik } from "formik";
import * as Yup from "yup";
import InputAdmin from "./AdminInput";
import api from "../../util/api";
import SelectInput from "./SelectInput";
import slugify from "slugify";

const CreateSubCategory = (categories) => {
    const [name, setName] = useState();
    const [parent, setParent] = useState();
    const validate = Yup.object({
        name: Yup.string()
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
        console.log(name);
        try {
            const slug = slugify(name);            
            const { data } = await api.post("/admin/subcategory", {
                name,
                parent,
                slug
            });

            //setSubCategories(data.subCategory);
            setName("");
            setParent("");
        } catch (error) {
            console.log(error.response.data.message);
        }
    };

    const handleChange = (e) => {
        setName(e.target.value);
    };

    return (
        <div className="mt-4">
            <div className="flex p-2 border-b pb-1 font-semibold">
                Create a Sub-Category
            </div>
            <Formik
                enableReinitialize
                initialValues={{ name, parent }}
                validationSchema={validate}
                onSubmit={() => submitHandler()}
            >
                {(form) => (
                    <Form className="flex-1 flex-col">
                        <InputAdmin
                            type="text"
                            name="name"
                            label="Sub-Category"
                            placeholder="sub Category Name"
                            icon="category"
                            onChange={handleChange}
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
    );
};

export default CreateSubCategory;
