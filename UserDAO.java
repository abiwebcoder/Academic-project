// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.fraud.dao;

import com.fraud.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
   public UserDAO() {
   }

   public User validateUser(String var1, String var2) {
      String var3 = "SELECT id, username, password, full_name, created_at FROM users WHERE username = ? AND password = ?";

      try {
         Connection var4 = DBConnection.getConnection();

         User var7;
         label114: {
            try {
               PreparedStatement var5;
               label106: {
                  var5 = var4.prepareStatement(var3);

                  try {
                     var5.setString(1, var1);
                     var5.setString(2, var2);
                     ResultSet var6 = var5.executeQuery();

                     label89: {
                        try {
                           if (var6.next()) {
                              var7 = new User(var6.getInt("id"), var6.getString("username"), var6.getString("password"), var6.getString("full_name"), var6.getTimestamp("created_at"));
                              break label89;
                           }
                        } catch (Throwable var12) {
                           if (var6 != null) {
                              try {
                                 var6.close();
                              } catch (Throwable var11) {
                                 var12.addSuppressed(var11);
                              }
                           }

                           throw var12;
                        }

                        if (var6 != null) {
                           var6.close();
                        }
                        break label106;
                     }

                     if (var6 != null) {
                        var6.close();
                     }
                  } catch (Throwable var13) {
                     if (var5 != null) {
                        try {
                           var5.close();
                        } catch (Throwable var10) {
                           var13.addSuppressed(var10);
                        }
                     }

                     throw var13;
                  }

                  if (var5 != null) {
                     var5.close();
                  }
                  break label114;
               }

               if (var5 != null) {
                  var5.close();
               }
            } catch (Throwable var14) {
               if (var4 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var9) {
                     var14.addSuppressed(var9);
                  }
               }

               throw var14;
            }

            if (var4 != null) {
               var4.close();
            }

            return null;
         }

         if (var4 != null) {
            var4.close();
         }

         return var7;
      } catch (SQLException var15) {
         System.err.println("Error validating user: " + var15.getMessage());
         var15.printStackTrace();
         return null;
      }
   }
}
