import Layout from "components/profile/Layout";
import api from "util/api";

const Profile = ({ user, tab, orders }) => {
    return (
        <>
            <Layout user={user} tab={tab} title={`${user.name}'s Profile`}>
            <div className="text-center">
                    <h2 className="text-4xl font-bold mb-6">My Profile</h2>
            </div>
            </Layout>
        </>
    );
};

export default Profile;

export const loader = (authContext) => {

    return async ({params, request}) => {
    
        const { currentUser } = authContext;

        if (!currentUser)
            return 

        const searchParams = new URL(request.url).searchParams;

        try {

            const { data } = await api.get("/profile/address", null,
            { params : { userId : currentUser.username,             
                        }
            } 
);                                   
        
            return data;    
        
        } catch (error) {
            console.log("erorr >>>", error.response.data.message);
        }
    };
}

// export async function getServerSideProps(context) {
//     db.connectDb();
//     const { query } = context;
//     const session = await getSession(context);
//     const user = session?.user;
//     const tab = query.tab || 0;

//     if (!session) {
//         return {
//             redirect: {
//                 destination: "/",
//             },
//         };
//     }

//     return {
//         props: {
//             user,
//             tab,
//         },
//     };
// }
