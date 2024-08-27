import {CollisionSummaryType} from "../ServerTypes";
import ServerConfig from "../ServerConfig";

const readCollisionSummary = (accessToken:String):Promise<CollisionSummaryType> => {
    return new Promise((resolve, reject) => {
        const serverinput = {
            accessToken: accessToken
        };
        fetch(ServerConfig.address + "/api/admin/collisionSummary", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(serverinput)
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    return response.text().then(errorText => {
                        throw new Error(errorText);
                    });

                }
            })
            .then(json => {
                resolve(json);
            })
            .catch(error => {
                reject(error.message);
            });
    })
};

export default readCollisionSummary;