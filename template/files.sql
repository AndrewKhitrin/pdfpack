select r2_files.id as id
  from r2_files
 where id not in
       (
        --отберём все файлы, которые участвовали в подписании, а затем отберём всё, кроме таких файлов:
        --0) Подписание собственно файла
        select id
          from r2_files f
         where ((select id_class from sys_attributes where id = f.id_attr),
                f.id_record) in (select id_class, id_object from esign)
        union all
        --1) документ  - подписывается коммуникант. Дата последнего редактирования подписи - больше даты добавления файла
        select id
          from r2_files f
         where f.id_attr = 645
           and exists (select 1
                  from r2_communicant, esign
                 where r2_communicant.id_doc = f.id_record
                   and r2_communicant.id = esign.id_object
                   and esign.id_class = 51
                   and esign.dt_edit > f.dt_create)
        union all
        --2) Операция над документом 
        select id
          from r2_files f
         where id_attr = 10558
           and exists (select 1
                  from esign
                 where esign.id_object = f.id_record
                   and esign.id_class = 16
                   and esign.dt_edit > f.dt_create)
        union all
        --3) Резолюция   --файл приложен к документу
        select id
          from r2_files f
         where id_attr = 645
           and exists (select 1
                  from r2_resolutions, esign
                 where r2_resolutions.id_doc = f.id_record
                   and esign.id_object = r2_resolutions.id
                   and esign.id_class = 44
                   and esign.dt_edit > f.dt_create)
        
        union all
        --4) Приложение при подписании операции  
        select id
          from r2_files f
         where id_attr = 653
           and exists
         (select 1
                  from doc_operation_list, r2_add_doc, esign
                 where doc_operation_list.id_doc = r2_add_doc.id_doc
                   and r2_add_doc.id = f.id_record
                   and doc_operation_list.id = esign.id_object
                   and esign.id_class = 16
                   and esign.dt_edit > f.dt_create)
        union all
        --5) Приложение при подписании документа
        select id
          from r2_files f
         where id_attr = 653
           and exists (select 1
                  from r2_add_doc, r2_communicant, esign
                 where r2_add_doc.id = f.id_record
                   and r2_communicant.id_doc = r2_add_doc.id_doc
                   and r2_communicant.id = esign.id_object
                   and esign.id_class = 51
                   and esign.dt_edit > f.dt_create)        
        ) and upper(file_ext) = 'PDF' and file_size > 0
        